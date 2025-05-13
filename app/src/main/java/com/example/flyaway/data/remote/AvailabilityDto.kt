package com.example.flyaway.data.remote.dto

data class AvailabilityDto(
    val hotelId: String,
    val availableRooms: List<String>
)