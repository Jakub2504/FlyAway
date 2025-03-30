package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "activities",
    foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId")]
)
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val dayId: String,
    val name: String,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val location: String
) 