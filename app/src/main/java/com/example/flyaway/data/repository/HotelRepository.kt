package com.example.flyaway.data.repository

import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Reservation

interface HotelRepository {
    suspend fun getHotels(): List<Hotel>
    suspend fun getAvailableHotels(city: String, startDate: String, endDate: String): List<Hotel>
    suspend fun reserveHotel(reservation: Reservation): Boolean
    suspend fun cancelReservation(reservationId: String): Boolean
    suspend fun getReservations(): List<Reservation>
}
