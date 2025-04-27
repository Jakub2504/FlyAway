package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "access_logs")
data class AccessLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val timestamp: LocalDateTime,
    val action: String
)

enum class AccessType {
    LOGIN,
    LOGOUT
} 