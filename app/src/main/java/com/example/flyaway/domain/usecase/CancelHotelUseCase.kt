package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.HotelRepository

class CancelHotelUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(reservationId: String): Boolean {
        return repository.cancelReservation(reservationId)
    }
}