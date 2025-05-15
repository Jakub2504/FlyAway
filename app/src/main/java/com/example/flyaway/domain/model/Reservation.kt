package com.example.flyaway.domain.model

data class Reservation(
    val reservationId: String,
    val hotelId: String,
    val roomId: String,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val price: Double
)
