package com.example.flyaway.data.repository

import com.example.flyaway.data.remote.HotelApi
import com.example.flyaway.data.remote.dto.HotelDto
import com.example.flyaway.data.remote.mapper.toDomain
import com.example.flyaway.data.HotelRepository

class HotelRepositoryImpl(private val api: HotelApi) : HotelRepository {

    override suspend fun getHotels(groupId: String): List<HotelDto> {
        return api.getHotels(groupId) // Asegúrate de que este método devuelva List<HotelDto>
    }

    override suspend fun getHotelAvailability(
        groupId: String,
        city: String,
        startDate: String,
        endDate: String
    ): List<HotelDto> {
        return api.getHotelAvailability(groupId, city, startDate, endDate) // Asegúrate de que este método devuelva List<HotelDto>
    }
}