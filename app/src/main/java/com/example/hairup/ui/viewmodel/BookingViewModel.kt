package com.example.hairup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.AddPointsRequest
import com.example.hairup.api.models.AddPointsResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.BookingRepository
import com.example.hairup.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val sessionManager: SessionManager,
    private val repository: BookingRepository = BookingRepository()
) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _barbers = MutableStateFlow<List<BarberItem>>(emptyList())
    val barbers: StateFlow<List<BarberItem>> = _barbers

    private val _availableSlots = MutableStateFlow<List<String>>(emptyList())
    val availableSlots: StateFlow<List<String>> = _availableSlots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _bookingSuccess = MutableStateFlow<Int?>(null)
    val bookingSuccess: StateFlow<Int?> = _bookingSuccess

    data class BarberItem(
        val id: Int, val name: String, val specialty: String, val initial: String
    )

    private var currentBarberId: Int? = null
    private var currentDate: String? = null

    init {
        loadServices()
        loadBarbers()
    }

    fun loadServices() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        repository.getServices(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { serviceResponses ->
                    _services.value = serviceResponses.map { it.toService() }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar servicios"
                    _isLoading.value = false
                })
            }
        }
    }

    fun loadBarbers() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        repository.getBarbers(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { barberResponses ->
                    val barberList = mutableListOf(
                        BarberItem(
                            id = -1,
                            name = "Sin preferencia",
                            specialty = "Cualquier profesional disponible",
                            initial = "?"
                        )
                    )
                    barberList.addAll(
                        barberResponses.map {
                            BarberItem(
                                id = it.id,
                                name = it.name,
                                specialty = "Peluquero/a",
                                initial = it.name.first().toString()
                            )
                        })
                    _barbers.value = barberList
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar barberos"
                })
            }
        }
    }

    fun loadAvailability(barberId: Int, date: String) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        if (barberId == -1) {
            _availableSlots.value = generateDefaultTimeSlots()
            return
        }

        currentBarberId = barberId
        currentDate = date
        _isLoading.value = true

        repository.getBarberAvailability(token, barberId, date) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { availability ->
                    val slots = availability.availableSlots.filter { it.available }
                        .map { it.time.substring(0, 5) }
                    _availableSlots.value = slots
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar disponibilidad"
                    _isLoading.value = false
                })
            }
        }
    }

    fun createBooking(
        serviceId: Int, date: String, time: String, barberId: Int, callback: (Boolean) -> Unit
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            callback(false)
            return
        }

        val finalBarberId = if (barberId == -1) 7 else barberId

        _isLoading.value = true

        repository.createBooking(token, serviceId, date, time, finalBarberId) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { bookingId ->
                    // Buscar el servicio para obtener los puntos
                    val service = _services.value.find { it.id == serviceId }
                    val pointsToAdd = service?.xp ?: 0

                    if (pointsToAdd > 0) {
                        _bookingSuccess.value = bookingId
                        _isLoading.value = false
                        callback(true)

                        addPointsForBooking(serviceId, pointsToAdd)
                    } else {
                        _bookingSuccess.value = bookingId
                        _isLoading.value = false
                        callback(true)
                    }
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al crear la cita"
                    _isLoading.value = false
                    callback(false)
                })
            }
        }
    }

    private fun addPointsForBooking(serviceId: Int, points: Int) {
        val token = sessionManager.getToken() ?: return

        com.example.hairup.data.repository.ShopRepository()


        val request = AddPointsRequest(points)

        val call =
            com.example.hairup.api.RetrofitClient.apiService.addPoints("Bearer $token", request)
        call.enqueue(object : retrofit2.Callback<AddPointsResponse> {
            override fun onResponse(
                call: retrofit2.Call<AddPointsResponse>,
                response: retrofit2.Response<AddPointsResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.success) {
                            sessionManager.getUser()?.let { user ->
                                val updatedUser = user.copy(
                                    xp = body.newXp, points = body.newPoints
                                )
                                sessionManager.saveAuthData(token, updatedUser)
                            }
                            Log.d("BookingViewModel", "Puntos añadidos por cita: $points")
                        }
                    }
                }
            }

            override fun onFailure(
                call: retrofit2.Call<AddPointsResponse>,
                t: Throwable
            ) {
                Log.e("BookingViewModel", "Error al añadir puntos de cita", t)
            }
        })
    }

    fun resetBookingSuccess() {
        _bookingSuccess.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun generateDefaultTimeSlots(): List<String> {
        val slots = mutableListOf<String>()
        for (hour in 9..19) {
            slots.add(String.format("%02d:00", hour))
            if (hour < 19) slots.add(String.format("%02d:30", hour))
        }
        return slots
    }

    fun formatDateForApi(year: Int, month: Int, day: Int): String {
        return String.format("%04d-%02d-%02d", year, month + 1, day)
    }
}