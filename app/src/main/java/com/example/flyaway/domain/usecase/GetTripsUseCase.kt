package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.TripRepository
import com.example.flyaway.domain.model.Trip
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import android.util.Log

/**
 * Caso de uso para obtener todos los viajes.
 * Sigue el principio de responsabilidad única, encapsulando una operación específica.
 */
@ViewModelScoped
class GetTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(userId: String): Flow<List<Trip>> {
        Log.d("GetTripsUseCase", "Obteniendo viajes para usuario: $userId")
        return tripRepository.getTripsByUserId(userId)
            .onEach { trips ->
                Log.d("GetTripsUseCase", "Viajes obtenidos: ${trips.size}")
            }
    }
} 