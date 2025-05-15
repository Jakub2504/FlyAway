package com.example.flyaway.domain.model

data class Room(
    val id: String,
    val type: String,
    val price: Double,
    val capacity: Int,
    val imageUrls: List<String>
)
