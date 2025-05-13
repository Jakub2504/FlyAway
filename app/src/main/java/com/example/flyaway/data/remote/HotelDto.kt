package com.example.flyaway.data.remote.dto

data class HotelDto(
    val id: String,
    val name: String,
    val rooms: List<RoomDto> // Asegúrate de que RoomDto esté definido
)