package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_access_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AppAccessLogEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val action: String,
    val timestamp: String
) 