package com.example.flyaway.data.remote.mapper

import com.example.flyaway.data.model.Hotel
import com.example.flyaway.data.remote.dto.HotelDto

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = this.id,
        name = this.name,
        rooms = this.rooms.map { it.toDomain() }
    )
}