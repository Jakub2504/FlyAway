package com.example.flyaway.data.repository

import com.example.flyaway.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val tripRepositoryImpl: TripRepositoryImpl
) {
    fun getTripsByUserId(userId: String): Flow<List<Trip>> {
        return tripRepositoryImpl.getAllTrips(userId)
    }

    fun getTripById(tripId: String, userId: String): Flow<Trip?> {
        return tripRepositoryImpl.getTripById(tripId, userId)
    }

    suspend fun saveTrip(trip: Trip, userId: String): Trip {
        return tripRepositoryImpl.saveTrip(trip, userId)
    }

    suspend fun deleteTrip(tripId: String, userId: String) {
        tripRepositoryImpl.deleteTrip(tripId, userId)
    }


} 