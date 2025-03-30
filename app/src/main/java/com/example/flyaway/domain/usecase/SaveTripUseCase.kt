package com.example.flyaway.domain.usecase


import com.example.flyaway.domain.model.Trip
import javax.inject.Inject
import com.example.flyaway.domain.repository.TripRepository

class SaveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip): Trip {
        return tripRepository.saveTrip(trip)
    }
} 