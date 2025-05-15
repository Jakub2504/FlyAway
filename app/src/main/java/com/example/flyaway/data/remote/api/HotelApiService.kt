package com.example.flyaway.data.remote.api

import com.example.flyaway.data.remote.dto.CancelReservationDto
import com.example.flyaway.data.remote.dto.HotelDto
import com.example.flyaway.data.remote.dto.ReservationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    @GET("hotels/{group_id}/hotels")
    suspend fun getHotels(@Path("group_id") groupId: String): List<HotelDto>

    @GET("hotels/{group_id}/availability")
    suspend fun getAvailability(
        @Path("group_id") groupId: String,
        @Query("city") city: String,
        @Query("start") startDate: String,
        @Query("end") endDate: String
    ): List<HotelDto>

    @POST("hotels/{group_id}/reserve")
    suspend fun reserveHotel(
        @Path("group_id") groupId: String,
        @Body reservation: ReservationDto
    ): Response<Unit>

    @GET("hotels/{group_id}/reservations")
    suspend fun getReservations(@Path("group_id") groupId: String): List<ReservationDto>

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body cancelDto: CancelReservationDto
    ): Response<Unit>
}