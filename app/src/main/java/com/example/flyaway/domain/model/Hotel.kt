package com.example.flyaway.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val city: String,
    val description: String,
    val imageUrls: List<String>,
    val rooms: List<Room>
)
