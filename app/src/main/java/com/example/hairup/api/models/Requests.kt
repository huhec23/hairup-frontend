package com.example.hairup.api.models

data class LoginRequest(
    val email: String, val password: String
)

data class RegisterRequest(
    val email: String, val password: String, val name: String, val phone: String
)

data class UpdateProfileRequest(
    val name: String, val email: String, val phone: String
)

data class ChangePasswordRequest(
    val currentPassword: String, val newPassword: String
)

data class CreateAppointmentRequest(
    val serviceId: Int, val date: String, val time: String, val barberId: Int
)

data class UpdateAppointmentRequest(
    val date: String? = null, val time: String? = null, val status: Int? = null
)

data class RedeemRequest(
    val rewardId: Int
)

data class PurchaseItem(
    val productId: Int, val quantity: Int
)

data class AddPointsRequest(
    val points: Int
)

data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val image: String? = null,
    val available: Boolean = true,
    val points: Int = 0,
    val categoryId: Int? = null
)

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val image: String? = null,
    val available: Boolean? = null,
    val points: Int? = null,
    val categoryId: Int? = null
)

data class CreateServiceRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val duration: Int,
    val xp: Int
)

data class UpdateServiceRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val duration: Int? = null,
    val xp: Int? = null
)

data class ToggleAdminRequest(
    val userId: Int
)

data class ToggleActiveRequest(
    val userId: Int, val active: Boolean
)

data class CreateCategoryRequest(
    val name: String
)