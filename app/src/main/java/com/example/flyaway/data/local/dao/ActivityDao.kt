package com.example.flyaway.data.local.dao

import androidx.room.*
import com.example.flyaway.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE dayId = :dayId ORDER BY startTime ASC")
    fun getActivitiesByDayId(dayId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    fun getActivityById(activityId: String): Flow<ActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: String)
} 