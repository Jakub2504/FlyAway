package com.example.flyaway.data.remote

import com.example.flyaway.data.model.*
import com.example.flyaway.data.remote.dto.HotelDto
import retrofit2.http.*

interface HotelApi {

    // Obtener lista de hoteles
    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(
        @Path("group_id") groupId: String
    ): List<HotelDto>

    // Disponibilidad de hoteles
    @GET("hotels/{group_id}/availability")
    suspend fun getHotelAvailability(
        @Path("group_id") groupId: String,
        @Query("city") city: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<HotelDto>

    // Reservar habitación
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body reservationRequest: ReservationRequest
    ): ReservationResponse

    // Cancelar reserva
    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body cancelRequest: CancelRequest
    ): CancelResponse

    // Obtener lista de reservas
    @GET("hotels/{group_id}/reservations")
    suspend fun getReservations(
        @Path("group_id") groupId: String
    ): List<Reservation>

    // ADMIN: Obtener todas las reservas
    @GET("reservations")
    suspend fun getAllReservations(): List<Reservation>

    // ADMIN: Obtener detalles de una reserva
    @GET("reservations/{res_id}")
    suspend fun getReservationDetails(
        @Path("res_id") reservationId: String
    ): Reservation

    // ADMIN: Eliminar una reserva
    @DELETE("reservations/{res_id}")
    suspend fun deleteReservation(
        @Path("res_id") reservationId: String
    ): DeleteResponse
}
