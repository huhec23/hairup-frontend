package com.example.hairup.api.models

import com.example.hairup.model.AdminAppointment
import com.example.hairup.model.AdminUser
import com.example.hairup.model.MiniAppointment
import com.example.hairup.model.Product
import com.example.hairup.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val token: String, val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val email: String,
    val name: String,
    val xp: Int,
    val points: Int,
    val admin: Boolean,
    val phone: String,
    val created: String,
    val levelId: Int
) {
    fun toUser(): User = User(
        id = id,
        email = email,
        name = name,
        xp = xp,
        points = points,
        levelId = levelId,
        phone = phone,
        isAdmin = admin,
        password = ""
    )
}

data class AppointmentResponse(
    val id: Int,
    val serviceName: String,
    val serviceId: Int,
    val date: String,
    val time: String,
    val stylistName: String,
    val stylistId: Int,
    val status: Int,
    val price: Double,
    val duration: Int,
    val xpEarned: Int
)

data class LevelResponse(
    val id: Int,
    val name: String,
    @SerializedName("requiredXp") val requiredXp: Int,
    val reward: String
)

data class LevelsResponse(
    val data: List<LevelResponse>
)

data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val image: String?,
    val available: Boolean,
    val points: Int,
    @SerializedName("categoryId") val categoryId: Int?,
    @SerializedName("categoryName") val categoryName: String?
) {
    fun toProduct(): Product {
        return Product(
            id = id,
            name = name,
            description = description ?: "",
            price = price,
            image = image ?: "",
            available = available,
            points = points,
            categoryId = categoryId ?: 0,
            categoryName = categoryName ?: ""
        )
    }
}

data class ProductsResponse(
    val data: List<ProductResponse>
)

data class AppointmentsResponse(
    val data: List<AppointmentResponse>
)

data class ServiceResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val duration: Int,
    val xpReward: Int
) {
    fun toService(): com.example.hairup.model.Service {
        return com.example.hairup.model.Service(
            id = id,
            name = name,
            description = description ?: "",
            price = price,
            duration = duration,
            xp = xpReward
        )
    }
}

data class ServicesResponse(
    val data: List<ServiceResponse>
)

data class BarberResponse(
    val id: Int, val name: String, val email: String, val phone: String?
)

data class BarbersResponse(
    val data: List<BarberResponse>
)

data class TimeSlot(
    val time: String,
    val available: Boolean,
    val serviceId: Int? = null,
    val serviceName: String? = null,
    val duration: Int? = null
)

data class AvailabilityResponse(
    val barberId: Int, val barberName: String, val date: String, val availableSlots: List<TimeSlot>
)

data class RewardResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val pointsCost: Int,
    val minLevelId: Int,
    val available: Boolean
)

data class RewardsResponse(
    val data: List<RewardResponse>
)


data class RedeemResponse(
    val success: Boolean, val message: String, val newPoints: Int?
)

data class CategoryResponse(
    val id: Int, val name: String
)

data class CategoriesResponse(
    val data: List<CategoryResponse>
)

data class PurchaseResponse(
    val success: Boolean,
    val message: String,
    val xpEarned: Int,
    val pointsEarned: Int,
    val newXp: Int,
    val newPoints: Int
)

data class AddPointsResponse(
    val success: Boolean,
    val message: String,
    val xpEarned: Int,
    val pointsEarned: Int,
    val newXp: Int,
    val newPoints: Int
)

data class AdminAppointmentsResponse(
    val data: List<AdminAppointmentDetailResponse>
)

data class AdminAppointmentDetailResponse(
    val id: Int,
    val serviceName: String,
    val price: Double,
    val duration: Int,
    val clientName: String,
    val clientPhone: String?,
    val stylistName: String,
    val stylistId: Int,
    val date: String,
    val time: String,
    val status: Int
) {
    fun toAdminAppointment(): AdminAppointment = AdminAppointment(
        id = id,
        clientName = clientName,
        clientPhone = clientPhone,
        serviceName = serviceName,
        date = date,
        time = time,
        stylistId = stylistId,
        stylistName = stylistName,
        status = status,
        price = price,
        duration = duration
    )
}

data class DashboardStatsResponse(
    val totalToday: Int,
    val pendingToday: Int,
    val confirmedToday: Int,
    val totalStylists: Int,
    val activeStylists: Int,
    val todayAppointments: List<MiniAppointmentResponse>
)

data class MiniAppointmentResponse(
    val id: Int,
    val clientName: String,
    val serviceName: String,
    val time: String,
    val status: Int,
    val stylistName: String?
) {
    fun toMiniAppointment(): MiniAppointment = MiniAppointment(
        id = id,
        clientName = clientName,
        serviceName = serviceName,
        time = time,
        status = status,
        stylistName = stylistName
    )
}

data class AllUsersResponse(
    val data: List<UserAdminResponse>
)

data class UserAdminResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val xp: Int,
    val points: Int,
    val admin: Boolean,
    val levelId: Int,
    val created: String,
    val active: Boolean? = true
) {
    fun toAdminUser(): AdminUser = AdminUser(
        id = id,
        name = name,
        email = email,
        phone = phone ?: "",
        xp = xp,
        points = points,
        isAdmin = admin,
        levelId = levelId,
        level = getLevelName(levelId),
        isActive = active ?: true,
        totalBookings = 0
    )

    private fun getLevelName(levelId: Int): String {
        return when (levelId) {
            1 -> "Bronce"
            2 -> "Plata"
            3 -> "Oro"
            4 -> "Platino"
            else -> "Bronce"
        }
    }

}

data class CategorySuccessResponse(
    val success: Boolean, val message: String, val id: Int? = null
)