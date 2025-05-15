package com.example.flyaway.data.remote.mapper

import com.example.flyaway.data.remote.dto.ReservationDto
import com.example.flyaway.domain.model.Reservation

fun ReservationDto.toDomain(): Reservation = Reservation(
    reservationId = this.reservationId ?: "",
    hotelId = this.hotelId,
    roomId = this.roomId,
    userId = this.userId,
    startDate = this.startDate,
    endDate = this.endDate,
    price = this.price
)
