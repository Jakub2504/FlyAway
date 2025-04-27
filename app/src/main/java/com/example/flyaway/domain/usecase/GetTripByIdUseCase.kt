package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.TripRepository
import com.example.flyaway.domain.model.Trip
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener un viaje específico por su ID.
 */
@ViewModelScoped
class GetTripByIdUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(tripId: String, userId: String): Flow<Trip?> {
        return tripRepository.getTripById(tripId, userId)
    }
} 