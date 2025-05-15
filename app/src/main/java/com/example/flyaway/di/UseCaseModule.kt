package com.example.flyaway.di

import com.example.flyaway.data.repository.HotelRepository
import com.example.flyaway.domain.usecase.SearchHotelsUseCase
import com.example.flyaway.domain.usecase.ReserveHotelUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSearchHotelsUseCase(repository: HotelRepository): SearchHotelsUseCase {
        return SearchHotelsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideReserveHotelUseCase(repository: HotelRepository): ReserveHotelUseCase {
        return ReserveHotelUseCase(repository)
    }

}
