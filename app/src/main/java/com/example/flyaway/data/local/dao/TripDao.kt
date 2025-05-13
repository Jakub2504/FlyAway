package com.example.flyaway.data.local.dao

import android.util.Log
import androidx.room.*
import com.example.flyaway.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTripsByUserId(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId AND userId = :userId")
    fun getTripById(tripId: String, userId: String): Flow<TripEntity?>

    @Query("""
        SELECT id, userId, name, destination, 
        strftime('%d/%m/%Y', datetime(startDate/86400000, 'unixepoch')) as startDate,
        strftime('%d/%m/%Y', datetime(endDate/86400000, 'unixepoch')) as endDate,
        strftime('%d/%m/%Y', datetime(createdAt/86400000, 'unixepoch')) as createdAt
        FROM trips 
        WHERE userId = :userId
        ORDER BY createdAt DESC
    """)
    fun getAllTripsWithFormattedDatesByUserId(userId: String): Flow<List<TripEntity>>

    @Query("""
        SELECT id, userId, name, destination, 
        strftime('%d/%m/%Y', datetime(startDate/86400000, 'unixepoch')) as startDate,
        strftime('%d/%m/%Y', datetime(endDate/86400000, 'unixepoch')) as endDate,
        strftime('%d/%m/%Y', datetime(createdAt/86400000, 'unixepoch')) as createdAt
        FROM trips 
        WHERE id = :tripId AND userId = :userId
    """)
    fun getTripByIdWithFormattedDates(tripId: String, userId: String): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity) {
        try {
            // Validar datos
            if (trip.name.isBlank() || trip.destination.isBlank()) {
                throw IllegalArgumentException("Los campos no pueden estar vacíos")
            }
            if (trip.endDate.isBefore(trip.startDate)) {
                throw IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio")
            }
            // Insertar
            insertTripInternal(trip)
        } catch (e: Exception) {
            Log.e("TripDao", "Error al insertar viaje", e)
            throw e
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripInternal(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity) {
        try {
            // Validar datos
            if (trip.name.isBlank() || trip.destination.isBlank()) {
                throw IllegalArgumentException("Los campos no pueden estar vacíos")
            }
            if (trip.endDate.isBefore(trip.startDate)) {
                throw IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio")
            }
            // Actualizar
            updateTripInternal(trip)
        } catch (e: Exception) {
            Log.e("TripDao", "Error al actualizar viaje", e)
            throw e
        }
    }

    @Update
    suspend fun updateTripInternal(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId AND userId = :userId")
    suspend fun deleteTripById(tripId: String, userId: String)
} 