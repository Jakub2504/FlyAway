package com.example.flyaway.di

import com.example.flyaway.BuildConfig
import com.example.flyaway.data.remote.api.HotelApiService
import com.example.flyaway.data.repository.HotelRepository
import com.example.flyaway.data.repository.HotelRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideHotelRepository(api: HotelApiService): HotelRepository {
        return HotelRepositoryImpl(api, BuildConfig.GROUP_ID)
    }
}
