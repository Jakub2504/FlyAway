package com.example.flyaway.domain.usecase

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SaveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip, userId: String) {
        tripRepository.saveTrip(trip, userId)
    }
} 