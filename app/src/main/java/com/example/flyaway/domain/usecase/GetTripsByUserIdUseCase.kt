package com.example.flyaway.domain.usecase

import com.example.flyaway.data.repository.TripRepository
import com.example.flyaway.domain.model.Trip
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetTripsByUserIdUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    operator fun invoke(userId: String): Flow<List<Trip>> {
        return tripRepository.getTripsByUserId(userId)
    }
} 