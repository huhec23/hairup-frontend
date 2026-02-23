package com.example.hairup.model

import androidx.compose.ui.graphics.Color

data class AdminUser(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val xp: Int,
    val points: Int,
    val isAdmin: Boolean,
    val levelId: Int,
    val level: String,
    val isActive: Boolean,
    val totalBookings: Int
) {
    val levelColor: Color
        get() = when (level) {
            "Platino" -> Color(0xFFB9F2FF)
            "Oro" -> Color(0xFFFFD700)
            "Plata" -> Color(0xFFC0C0C0)
            "Bronce" -> Color(0xFFCD7F32)
            else -> Color(0xFFB0B0B0)
        }

    val avatarColor: Color
        get() = when {
            !isActive -> Color(0xFFB0B0B0).copy(alpha = 0.5f)
            isAdmin -> Color(0xFFD4AF37)
            else -> Color(0xFF64B5F6)
        }

    val nameColor: Color
        get() = if (isActive) Color(0xFFFFFFFF) else Color(0xFFB0B0B0)

    val initials: String
        get() = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}