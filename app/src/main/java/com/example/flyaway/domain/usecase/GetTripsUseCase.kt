package com.example.flyaway.domain.usecase

import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los viajes.
 * Sigue el principio de responsabilidad única, encapsulando una operación específica.
 */
class GetTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        return tripRepository.getAllTrips()
    }
} 