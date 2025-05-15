package com.example.flyaway.data.remote.mapper

import com.example.flyaway.data.remote.dto.HotelDto
import com.example.flyaway.domain.model.Hotel

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id,
        name = this.name,
        city = this.city,
        description = this.description,
        imageUrls = this.imageUrls,
        rooms = this.rooms.map { it.toDomain() }
    )
}

