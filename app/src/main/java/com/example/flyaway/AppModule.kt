package com.example.flyaway

import android.content.Context
import com.example.flyaway.data.repository.TripRepositoryImpl
import com.example.flyaway.domain.repository.TripRepository
import com.example.flyaway.utils.PreferencesRepository
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
    

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindsModule {
        @Binds
        @Singleton
        abstract fun bindTripRepository(
            tripRepositoryImpl: TripRepositoryImpl
        ): TripRepository
    }
} 