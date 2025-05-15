package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.HotelRepository
import com.example.flyaway.domain.model.Reservation


class ReserveHotelUseCase(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(reservation: Reservation): Boolean {
        return repository.reserveHotel(reservation)
    }
}
