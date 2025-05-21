package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "trips",
    indices = [Index("userId")]
)
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDate,
    val images: List<String> = emptyList() // Nueva columna
)