package com.example.flyaway.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthException) {
            when (e) {
                is FirebaseAuthInvalidUserException -> throw Exception("No existe una cuenta con este email")
                is FirebaseAuthInvalidCredentialsException -> throw Exception("Contraseña incorrecta")
                else -> throw Exception("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    suspend fun register(email: String, password: String) {
        try {
            // Intentamos crear el usuario directamente
            // Firebase lanzará una excepción si el email ya existe
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthException) {
            when (e) {
                is FirebaseAuthUserCollisionException -> throw Exception("Ya existe una cuenta con este email")
                else -> throw Exception("Error al crear la cuenta: ${e.message}")
            }
        }
    }

    suspend fun recoverPassword(email: String) {
        try {
            // Intentamos enviar el email de recuperación
            // Firebase lanzará una excepción si el email no existe
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthException) {
            when (e) {
                is FirebaseAuthInvalidUserException -> throw Exception("No existe una cuenta con este email")
                else -> throw Exception("Error al enviar el email de recuperación: ${e.message}")
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
} 