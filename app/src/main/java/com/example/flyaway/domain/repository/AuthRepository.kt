package com.example.flyaway.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun logout(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
} 