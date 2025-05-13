package com.example.flyaway.data.remote.mapper

import com.example.flyaway.data.model.Room
import com.example.flyaway.data.remote.dto.RoomDto

fun RoomDto.toDomain(): Room {
    return Room(
        id = this.id,
        name = this.name,
        price = this.price,
        availability = this.availability
    )
}