package com.example.flyaway.data.remote.dto

data class RoomDto(
    val id: String,
    val type: String,
    val price: Double,
    val capacity: Int,
    val imageUrls: List<String>
)
