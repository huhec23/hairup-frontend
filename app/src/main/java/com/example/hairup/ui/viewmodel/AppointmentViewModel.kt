package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AppointmentRepository
import com.example.hairup.model.BookingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AppointmentViewModel(
    private val sessionManager: SessionManager,
    private val repository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    private val _upcomingAppointments = MutableStateFlow<List<AppointmentItem>>(emptyList())
    val upcomingAppointments: StateFlow<List<AppointmentItem>> = _upcomingAppointments

    private val _pastAppointments = MutableStateFlow<List<AppointmentItem>>(emptyList())
    val pastAppointments: StateFlow<List<AppointmentItem>> = _pastAppointments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _cancelSuccess = MutableStateFlow(false)
    val cancelSuccess: StateFlow<Boolean> = _cancelSuccess

    data class AppointmentItem(
        val id: Int,
        val serviceName: String,
        val date: String,
        val dateFormatted: String,
        val time: String,
        val stylistName: String,
        val status: BookingStatus,
        val price: Double,
        val duration: Int,
        val xpEarned: Int
    )

    fun loadAppointments() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        repository.getUserAppointments(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { appointments ->
                    processAppointments(appointments)
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar citas"
                    _isLoading.value = false
                })
            }
        }
    }

    private fun processAppointments(appointments: List<AppointmentResponse>) {
        val upcoming = mutableListOf<AppointmentItem>()
        val past = mutableListOf<AppointmentItem>()

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val now = Calendar.getInstance()

        appointments.forEach { apt ->
            val status = BookingStatus.fromCode(apt.status)

            val appointmentCal = Calendar.getInstance()
            try {
                val dateParts = apt.date.split("-")
                if (dateParts.size == 3) {
                    appointmentCal.set(
                        dateParts[0].toInt(), dateParts[1].toInt() - 1, dateParts[2].toInt()
                    )
                }
            } catch (_: Exception) {
                upcoming.add(createAppointmentItem(apt, status))
                return@forEach
            }

            val timeParts = apt.time.split(":")
            val appointmentHour = if (timeParts.isNotEmpty()) timeParts[0].toInt() else 0
            val appointmentMinute = if (timeParts.size >= 2) timeParts[1].toInt() else 0

            appointmentCal.set(Calendar.HOUR_OF_DAY, appointmentHour)
            appointmentCal.set(Calendar.MINUTE, appointmentMinute)

            val isPast = when {
                status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED -> true
                appointmentCal.before(today) -> true
                appointmentCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) && appointmentCal.get(
                    Calendar.DAY_OF_YEAR
                ) == today.get(Calendar.DAY_OF_YEAR) && appointmentCal.before(now) && status != BookingStatus.PENDING -> true

                else -> false
            }

            val item = AppointmentItem(
                id = apt.id,
                serviceName = apt.serviceName,
                date = apt.date,
                dateFormatted = formatDate(apt.date),
                time = apt.time.take(5),
                stylistName = apt.stylistName,
                status = status,
                price = apt.price,
                duration = apt.duration,
                xpEarned = if (status == BookingStatus.COMPLETED) apt.xpEarned else 0
            )

            if (isPast) {
                past.add(item)
            } else {
                upcoming.add(item)
            }
        }

        _upcomingAppointments.value = upcoming.sortedBy { "${it.date} ${it.time}" }
        _pastAppointments.value = past.sortedByDescending { "${it.date} ${it.time}" }
    }

    private fun createAppointmentItem(
        apt: AppointmentResponse, status: BookingStatus
    ): AppointmentItem {
        return AppointmentItem(
            id = apt.id,
            serviceName = apt.serviceName,
            date = apt.date,
            dateFormatted = formatDate(apt.date),
            time = apt.time.take(5),
            stylistName = apt.stylistName,
            status = status,
            price = apt.price,
            duration = apt.duration,
            xpEarned = if (status == BookingStatus.COMPLETED) apt.xpEarned else 0
        )
    }

    fun cancelAppointment(appointmentId: Int) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _cancelSuccess.value = false

        repository.cancelAppointment(token, appointmentId) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = {
                    _cancelSuccess.value = true
                    loadAppointments()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cancelar cita"
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetCancelSuccess() {
        _cancelSuccess.value = false
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                val day = parts[2].toInt()

                val monthNames = listOf(
                    "Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre"
                )

                val dayOfWeek = getDayOfWeek(year, month, day)

                "$dayOfWeek $day de ${monthNames[month - 1]}, $year"
            } else {
                dateStr
            }
        } catch (_: Exception) {
            dateStr
        }
    }

    private fun getDayOfWeek(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Domingo"
        }
    }
}