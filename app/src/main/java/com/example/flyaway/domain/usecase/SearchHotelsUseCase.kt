package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.HotelRepository
import com.example.flyaway.domain.model.Hotel

class SearchHotelsUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(city: String, startDate: String, endDate: String): List<Hotel> {
        return repository.getAvailableHotels(city, startDate, endDate)
    }
}
