package com.example.flyaway.domain.repository

import com.example.flyaway.domain.model.Activity
import com.example.flyaway.domain.model.Day
import com.example.flyaway.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de viajes.
 * Define las operaciones disponibles sin especificar los detalles de implementación.
 */
interface TripRepository {
    
    // Operaciones para Trip
    fun getAllTrips(userId: String): Flow<List<Trip>>
    fun getTripById(tripId: String, userId: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip, userId: String): Trip
    suspend fun deleteTrip(tripId: String, userId: String)
    suspend fun createInitialDaysForTrip(trip: Trip, userId: String): Trip
    
    // Operaciones para Day
    suspend fun saveDay(day: Day, userId: String): Trip?
    suspend fun deleteDay(tripId: String, dayId: String, userId: String): Trip?
    
    // Operaciones para Activity
    suspend fun saveActivity(activity: Activity, userId: String): Day?
    suspend fun deleteActivity(dayId: String, activityId: String, userId: String): Day?
} 