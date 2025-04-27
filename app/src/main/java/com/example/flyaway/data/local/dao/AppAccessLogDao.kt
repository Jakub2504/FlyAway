package com.example.flyaway.data.local.dao

import androidx.room.*
import com.example.flyaway.data.local.entity.AppAccessLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppAccessLogDao {
    @Query("SELECT * FROM app_access_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAccessLogsByUserId(userId: String): Flow<List<AppAccessLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessLog(log: AppAccessLogEntity)

    @Query("DELETE FROM app_access_logs WHERE userId = :userId")
    suspend fun deleteAccessLogsByUserId(userId: String)
} 