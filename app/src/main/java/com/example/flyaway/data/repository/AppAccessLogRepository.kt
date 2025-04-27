package com.example.flyaway.data.repository

import com.example.flyaway.data.local.dao.AppAccessLogDao
import com.example.flyaway.data.local.entity.AppAccessLogEntity
import com.example.flyaway.domain.model.AppAccessLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAccessLogRepository @Inject constructor(
    private val appAccessLogDao: AppAccessLogDao
) {
    fun getAccessLogsByUserId(userId: String): Flow<List<AppAccessLog>> {
        return appAccessLogDao.getAccessLogsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun logAccess(userId: String, action: String) {
        val log = AppAccessLogEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            action = action,
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        appAccessLogDao.insertAccessLog(log)
    }

    suspend fun deleteAccessLogsByUserId(userId: String) {
        appAccessLogDao.deleteAccessLogsByUserId(userId)
    }

    private fun AppAccessLogEntity.toDomainModel(): AppAccessLog {
        return AppAccessLog(
            id = id,
            userId = userId,
            action = action,
            timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
} 