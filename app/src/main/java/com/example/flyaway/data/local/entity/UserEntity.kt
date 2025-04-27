package com.example.flyaway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val username: String,
    val birthdate: String,
    val country: String,
    val phoneNumber: String,
    val acceptEmails: Boolean
) 