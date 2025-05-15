package com.example.flyaway.data.remote.dto

data class ReservationDto(
    val reservationId: String?,
    val hotelId: String,
    val roomId: String,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val price: Double
)
