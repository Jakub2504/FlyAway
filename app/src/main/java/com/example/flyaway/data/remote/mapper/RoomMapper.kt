package com.example.flyaway.data.remote.mapper

import com.example.flyaway.data.remote.dto.RoomDto
import com.example.flyaway.domain.model.Room

fun RoomDto.toDomain(): Room {
    return Room(
        id = this.id,
        type = this.type,
        price = this.price,
        capacity = this.capacity,
        imageUrls = this.imageUrls
    )
}