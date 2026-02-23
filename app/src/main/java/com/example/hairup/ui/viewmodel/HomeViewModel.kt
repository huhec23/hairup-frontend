package com.example.hairup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.HomeRepository
import com.example.hairup.model.Level
import com.example.hairup.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val repository: HomeRepository = HomeRepository()
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState
    private val tag = "HomeViewModel"

    data class NextAppointment(
        val serviceName: String, val date: String, val time: String, val stylistName: String
    )

    data class HomeData(
        val user: User,
        val nextAppointment: NextAppointment?,
        val currentLevel: Level,
        val nextLevel: Level?
    )

    sealed class HomeState {
        object Loading : HomeState()
        data class Success(val data: HomeData) : HomeState()
        data class Error(val message: String) : HomeState()
    }

    fun loadHomeData() {
        val token = sessionManager.getToken()
        val user = sessionManager.getUser()

        if (token.isNullOrEmpty() || user == null) {
            _homeState.value = HomeState.Error("No hay sesión activa")
            return
        }

        _homeState.value = HomeState.Loading
        Log.d(tag, "Cargando datos para usuario: ${user.name}")

        var levelsLoaded = false
        var appointmentLoaded = false
        var levelsResult: Result<List<Level>>? = null
        var appointmentResult: Result<AppointmentResponse?>? = null

        fun tryCombineResults() {
            if (levelsLoaded && appointmentLoaded) {
                Log.d(tag, "Ambas llamadas completadas")

                if (levelsResult?.isFailure == true) {
                    val error = levelsResult!!.exceptionOrNull()
                    Log.e(tag, "Error en niveles: ${error?.message}")
                    _homeState.value = HomeState.Error(error?.message ?: "Error al cargar niveles")
                    return
                }

                if (appointmentResult?.isFailure == true) {
                    val error = appointmentResult!!.exceptionOrNull()
                    Log.e(tag, "Error en cita: ${error?.message}")
                    _homeState.value = HomeState.Error(error?.message ?: "Error al cargar cita")
                    return
                }

                val levels = levelsResult?.getOrNull() ?: emptyList()
                val appointment = appointmentResult?.getOrNull()

                Log.d(tag, "Niveles cargados: ${levels.size}")
                Log.d(tag, "Cita recibida: ${if (appointment != null) "Sí" else "No"}")

                val currentLevel = levels.find { it.id == user.levelId } ?: Level(
                    id = 1,
                    name = "Bronce",
                    required = 0,
                    reward = ""
                )

                val nextLevel = levels.find { it.id == user.levelId + 1 }

                Log.d(tag, "Nivel actual: ${currentLevel.name} (ID: ${currentLevel.id})")
                Log.d(tag, "Siguiente nivel: ${nextLevel?.name ?: "Ninguno"}")

                val nextAppointment = if (appointment != null) {
                    Log.d(tag, "Creando NextAppointment con: ${appointment.serviceName}")
                    NextAppointment(
                        serviceName = appointment.serviceName,
                        date = appointment.date,
                        time = appointment.time,
                        stylistName = appointment.stylistName
                    )
                } else {
                    Log.d(tag, "No hay cita, nextAppointment = null")
                    null
                }

                val homeData = HomeData(
                    user = user,
                    nextAppointment = nextAppointment,
                    currentLevel = currentLevel,
                    nextLevel = nextLevel
                )

                Log.d(tag, "HomeData creado correctamente")
                _homeState.value = HomeState.Success(homeData)
            }
        }

        repository.getLevels(token) { result ->
            viewModelScope.launch {
                levelsResult = result
                levelsLoaded = true
                Log.d(tag, "Niveles cargados, levelsLoaded = true")
                tryCombineResults()
            }
        }

        repository.getNextAppointment(token) { result ->
            viewModelScope.launch {
                appointmentResult = result
                appointmentLoaded = true
                Log.d(tag, "Cita cargada, appointmentLoaded = true")
                tryCombineResults()
            }
        }
    }
}