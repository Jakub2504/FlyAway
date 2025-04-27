package com.example.flyaway

import android.content.Context
import android.util.Log
import com.example.flyaway.data.repository.AuthRepositoryImpl
import com.example.flyaway.data.repository.TripRepositoryImpl
import com.example.flyaway.domain.repository.AuthRepository
import com.example.flyaway.domain.repository.TripRepository
import com.example.flyaway.utils.PreferencesRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo principal de Dagger-Hilt para proporcionar las dependencias generales de la aplicación.
 */
@Module(includes = [AppModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Dependencias proporcionadas con @Provides
    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp {
        Log.d("AppModule", "Inicializando FirebaseApp")
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d("AppModule", "Firebase no está inicializado, inicializando...")
                FirebaseApp.initializeApp(context)
            }
            FirebaseApp.getInstance()
        } catch (e: Exception) {
            Log.e("AppModule", "Error al inicializar FirebaseApp", e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth {
        Log.d("AppModule", "Proporcionando FirebaseAuth")
        return try {
            FirebaseAuth.getInstance(firebaseApp)
        } catch (e: Exception) {
            Log.e("AppModule", "Error al obtener FirebaseAuth", e)
            throw e
        }
    }

    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(
        firebaseAuth: FirebaseAuth,
        @ApplicationContext context: Context
    ): AuthRepositoryImpl {
        Log.d("AppModule", "Proporcionando AuthRepositoryImpl")
        return AuthRepositoryImpl(firebaseAuth, context)
    }
    

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindsModule {
        @Binds
        @Singleton
        abstract fun bindTripRepository(
            tripRepositoryImpl: TripRepositoryImpl
        ): TripRepository

        @Binds
        @Singleton
        abstract fun bindAuthRepository(
            authRepositoryImpl: AuthRepositoryImpl
        ): AuthRepository
    }
} 