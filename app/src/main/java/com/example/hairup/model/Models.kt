package com.example.hairup.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val email: String,
    val points: Int,
    val level: String = "Bronce"
)

data class Appointment(
    val id: String,
    val serviceName: String,
    val date: Date,
    val stylistName: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val pointsReward: Int,
    val imageUrl: String = ""
)
