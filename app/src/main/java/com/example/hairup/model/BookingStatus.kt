package com.example.hairup.model

enum class BookingStatus(val code: Int) {
    PENDING(0), CONFIRMED(1), COMPLETED(2), CANCELLED(3);

    companion object {
        fun fromCode(code: Int): BookingStatus = entries.firstOrNull { it.code == code } ?: PENDING

    }
}
