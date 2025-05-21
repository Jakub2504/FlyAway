package com.example.flyaway.data.local.dao

import androidx.room.*
import com.example.flyaway.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTripsByUserId(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId AND userId = :userId")
    fun getTripById(tripId: String, userId: String): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId AND userId = :userId")
    suspend fun deleteTripById(tripId: String, userId: String)
}