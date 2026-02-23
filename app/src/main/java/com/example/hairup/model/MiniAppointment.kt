package com.example.hairup.model

import androidx.compose.ui.graphics.Color

data class MiniAppointment(
    val id: Int,
    val clientName: String,
    val serviceName: String,
    val time: String,
    val status: Int,
    val stylistName: String?
) {
    val statusText: String
        get() = when (status) {
            0 -> "Pendiente"
            1 -> "Confirmada"
            2 -> "Completada"
            3 -> "Cancelada"
            else -> "Desconocido"
        }
    val statusColor: Color
        get() = when (status) {
            0 -> Color(0xFFFFC107)  // Amber
            1 -> Color(0xFF4CAF50)  // Green
            2 -> Color(0xFF9E9E9E)  // Gray
            3 -> Color(0xFFE53935)  // Red
            else -> Color(0xFFB0B0B0)
        }
}