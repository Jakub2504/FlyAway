package com.example.flyaway.data.repository

import com.example.flyaway.data.remote.dto.HotelDto

interface HotelRepository {
    suspend fun getHotels(groupId: String): List<HotelDto>
    suspend fun getHotelAvailability(
        groupId: String,
        city: String,
        startDate: String,
        endDate: String
    ): List<HotelDto>
}