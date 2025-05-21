package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Room

@Entity(tableName = "Reservation")
data class ReservationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val tripId: String,
    val reservationDate: String,
    val status: String,
    val hotel: Hotel?, // Asegúrate de que Hotel sea serializable
    val room: Room?    // Asegúrate de que Room sea serializable
)