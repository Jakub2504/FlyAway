package com.example.flyaway.domain.usecase

import com.example.flyaway.domain.repository.TripRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar un viaje por su ID.
 */
class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(tripId: String) {
        tripRepository.deleteTrip(tripId)
    }
} 