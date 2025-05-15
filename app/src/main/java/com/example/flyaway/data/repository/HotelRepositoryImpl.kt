package com.example.flyaway.data.repository

import com.example.flyaway.BuildConfig
import com.example.flyaway.data.remote.api.HotelApiService
import com.example.flyaway.data.remote.mapper.toDomain
import com.example.flyaway.data.remote.dto.ReservationDto
import com.example.flyaway.data.remote.dto.CancelReservationDto
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Reservation

class HotelRepositoryImpl(
    private val api: HotelApiService,

    private val groupId: String

) : HotelRepository {

    override suspend fun getHotels(): List<Hotel> {
        return api.getHotels(groupId).map { it.toDomain() }
    }

    override suspend fun getAvailableHotels(city: String, startDate: String, endDate: String): List<Hotel> {
        return api.getAvailability(groupId, city, startDate, endDate).map { it.toDomain() }
    }

    override suspend fun reserveHotel(reservation: Reservation): Boolean {
        val dto = ReservationDto(
            reservationId = null,
            hotelId = reservation.hotelId,
            roomId = reservation.roomId,
            userId = reservation.userId,
            startDate = reservation.startDate,
            endDate = reservation.endDate,
            price = reservation.price
        )
        return api.reserveHotel(groupId, dto).isSuccessful
    }

    override suspend fun cancelReservation(reservationId: String): Boolean {
        return api.cancelReservation(groupId, CancelReservationDto(reservationId)).isSuccessful
    }

    override suspend fun getReservations(): List<Reservation> {
        return api.getReservations(groupId).map { it.toDomain() }
    }
}
