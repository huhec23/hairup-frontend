package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AdminAppointmentRepository
import com.example.hairup.model.AdminAppointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAppointmentViewModel(
    private val sessionManager: SessionManager,
    private val repository: AdminAppointmentRepository = AdminAppointmentRepository()
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<AdminAppointment>>(emptyList())
    val appointments: StateFlow<List<AdminAppointment>> = _appointments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    private val todayDate: String
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date())
        }

    val todayAppointments: List<AdminAppointment>
        get() = _appointments.value.filter { it.date == todayDate }

    val upcomingAppointments: List<AdminAppointment>
        get() = _appointments.value.filter {
            it.date > todayDate && it.status != 2 && it.status != 3
        }

    val pastAppointments: List<AdminAppointment>
        get() = _appointments.value.filter {
            it.date < todayDate || it.status == 2 || it.status == 3
        }

    fun loadAppointments(stylistId: Int = 0) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        repository.getAllAppointments(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { appointments ->
                    _appointments.value = if (stylistId == 0) {
                        appointments
                    } else {
                        appointments.filter { it.stylistId == stylistId }
                    }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar citas"
                    _isLoading.value = false
                })
            }
        }
    }

    fun confirmAppointment(appointmentId: Int) {
        updateAppointmentStatus(appointmentId, 1)
    }

    fun cancelAppointment(appointmentId: Int) {
        updateAppointmentStatus(appointmentId, 3)
    }

    fun completeAppointment(appointmentId: Int) {
        updateAppointmentStatus(appointmentId, 2)
    }

    private fun updateAppointmentStatus(appointmentId: Int, status: Int) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        repository.updateAppointmentStatus(token, appointmentId, status) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = {
                    _successMessage.value = when (status) {
                        1 -> "Cita confirmada"
                        2 -> "Cita completada"
                        3 -> "Cita cancelada"
                        else -> "Estado actualizado"
                    }
                    _operationSuccess.value = true
                    loadAppointments()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al actualizar cita"
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetStates() {
        _errorMessage.value = null
        _successMessage.value = null
        _operationSuccess.value = false
        _isLoading.value = false
    }
}