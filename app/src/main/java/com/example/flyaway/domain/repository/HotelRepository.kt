package com.example.flyaway.domain.repository

import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Reservation
import com.example.flyaway.domain.model.ReserveRequest

//interface HotelRepository {
//    suspend fun getHotels(groupId: String): List<Hotel>
//    suspend fun getAvailability(groupId: String, start: String, end: String): List<Hotel>
//    suspend fun reserve(groupId: String, request: ReserveRequest): Reservation
//
//}


interface HotelRepository {

    /* ---------- Hotels & Availability ---------- */
    suspend fun getHotels(groupId: String): List<Hotel>
    suspend fun getAvailability(
        groupId: String,
        start: String,
        end: String,
        hotelId: String? = null,
        city: String? = null
    ): List<Hotel>

    /* ---------- Make & cancel reservation (by group) ---------- */
    suspend fun reserve(groupId: String, request: ReserveRequest): Reservation
    suspend fun cancel(groupId: String, request: ReserveRequest): String   // returns message

    /* ---------- Reservations queries ---------- */
    suspend fun getGroupReservations(
        groupId: String,
        guestEmail: String? = null
    ): List<Reservation>

    suspend fun getAllReservations(): Map<String, List<Reservation>>

    /* ---------- Operations by reservation-id ---------- */
    suspend fun getReservationById(resId: String): Reservation
    suspend fun cancelById(resId: String): Reservation
}