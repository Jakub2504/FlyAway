package com.example.flyaway.data.local.dao

import androidx.room.*
import com.example.flyaway.data.local.entity.AccessLogEntity
import com.example.flyaway.data.local.entity.AccessType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface AccessLogDao {
    @Query("SELECT * FROM access_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAccessLogsByUserId(userId: String): Flow<List<AccessLogEntity>>

    @Insert
    suspend fun insertAccessLog(accessLog: AccessLogEntity)

    @Query("DELETE FROM access_logs WHERE userId = :userId")
    suspend fun deleteAccessLogsByUserId(userId: String)

    @Query("""
        SELECT * FROM access_logs 
        WHERE userId = :userId 
        AND action = :action 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getLastAccessLog(userId: String, action: String): AccessLogEntity?

    @Query("""
        SELECT * FROM access_logs 
        WHERE userId = :userId 
        AND timestamp >= :startDate 
        AND timestamp <= :endDate 
        ORDER BY timestamp DESC
    """)
    fun getAccessLogsByDateRange(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<AccessLogEntity>>
} 