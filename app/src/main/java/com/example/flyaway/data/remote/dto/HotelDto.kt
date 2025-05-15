package com.example.flyaway.data.remote.dto

data class HotelDto(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val imageUrls: List<String>,
    val rooms: List<RoomDto>
)
