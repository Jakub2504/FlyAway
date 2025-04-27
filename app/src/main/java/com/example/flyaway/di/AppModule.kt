package com.example.flyaway.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Mover la provisión de FirebaseAuth a FirebaseModule
} 