package com.example.hairup.model

data class Booking(
    val id: Int = 0,
    val date: String = "",
    val time: String = "",
    val status: Int = BookingStatus.PENDING.code,
    val userId: Int = 0,
    val serviceId: Int = 0
) {
    fun getStatus(): BookingStatus = BookingStatus.fromCode(status)
}
