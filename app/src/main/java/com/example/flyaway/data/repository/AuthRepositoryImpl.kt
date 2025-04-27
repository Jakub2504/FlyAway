package com.example.flyaway.data.repository

import android.content.Context
import android.util.Log
import com.example.flyaway.domain.repository.AuthRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
) : AuthRepository {

    init {
        Log.d("AuthRepositoryImpl", "Inicializando AuthRepositoryImpl")
        try {
            // Verificar que Firebase esté inicializado
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.e("AuthRepositoryImpl", "Firebase no está inicializado")
                throw IllegalStateException("Firebase no está inicializado")
            }
            Log.d("AuthRepositoryImpl", "Firebase está inicializado correctamente")
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error al verificar Firebase", e)
        }
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d("AuthRepositoryImpl", "Intentando login con email: $email")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("AuthRepositoryImpl", "Login exitoso para email: $email")
            Result.success(result.user!!)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d("AuthRepositoryImpl", "Intentando registro con email: $email")
            
            // Verificar que Firebase esté inicializado
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.e("AuthRepositoryImpl", "Firebase no está inicializado en register")
                throw IllegalStateException("Firebase no está inicializado")
            }
            
            // Verificar que firebaseAuth esté inicializado
            if (firebaseAuth == null) {
                Log.e("AuthRepositoryImpl", "FirebaseAuth es null")
                throw IllegalStateException("FirebaseAuth no está inicializado")
            }
            
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Log.d("AuthRepositoryImpl", "Registro exitoso para email: $email")
            Result.success(result.user!!)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error en registro: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            Log.d("AuthRepositoryImpl", "Intentando logout")
            firebaseAuth.signOut()
            Log.d("AuthRepositoryImpl", "Logout exitoso")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error en logout: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            Log.d("AuthRepositoryImpl", "Intentando enviar email de reset para: $email")
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d("AuthRepositoryImpl", "Email de reset enviado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error al enviar email de reset: ${e.message}", e)
            Result.failure(e)
        }
    }
} 