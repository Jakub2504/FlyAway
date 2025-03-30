package com.example.flyaway.data.local.dao

import androidx.room.*
import com.example.flyaway.data.local.entity.DayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {
    @Query("SELECT * FROM days WHERE tripId = :tripId ORDER BY dayNumber ASC")
    fun getDaysByTripId(tripId: String): Flow<List<DayEntity>>

    @Query("SELECT * FROM days WHERE id = :dayId")
    fun getDayById(dayId: String): Flow<DayEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: DayEntity)

    @Update
    suspend fun updateDay(day: DayEntity)

    @Delete
    suspend fun deleteDay(day: DayEntity)

    @Query("DELETE FROM days WHERE id = :dayId")
    suspend fun deleteDayById(dayId: String)
} 