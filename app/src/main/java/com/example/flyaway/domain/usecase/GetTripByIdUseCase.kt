package com.example.flyaway.domain.usecase

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener un viaje específico por su ID.
 */
class GetTripByIdUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(tripId: String): Flow<Trip?> {
        return tripRepository.getTripById(tripId)
    }
} 