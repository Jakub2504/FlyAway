package com.example.flyaway.domain.usecase

import com.example.flyaway.domain.repository.TripRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * Caso de uso para eliminar un viaje por su ID.
 */
@ViewModelScoped
class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(tripId: String, userId: String) {
        tripRepository.deleteTrip(tripId, userId)
    }
} 