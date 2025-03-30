package com.example.flyaway.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flyaway.data.local.FlyAwayDatabase
import com.example.flyaway.data.local.dao.ActivityDao
import com.example.flyaway.data.local.dao.DayDao
import com.example.flyaway.data.local.dao.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger-Hilt para proporcionar las dependencias relacionadas con la base de datos.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Proporciona la instancia de la base de datos Room.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlyAwayDatabase {
        return Room.databaseBuilder(
            context,
            FlyAwayDatabase::class.java,
            "flyaway_database"
        )
            .fallbackToDestructiveMigration() // Solo para desarrollo
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    android.util.Log.d("DatabaseModule", "Base de datos creada")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    android.util.Log.d("DatabaseModule", "Base de datos abierta")
                }
            })
            .build()
    }


    @Provides
    @Singleton
    fun provideTripDao(database: FlyAwayDatabase): TripDao = database.tripDao()

    /**
     * Proporciona el DAO para la entidad Day.
     */
    @Provides
    @Singleton
    fun provideDayDao(database: FlyAwayDatabase): DayDao = database.dayDao()

    /**
     * Proporciona el DAO para la entidad Activity.
     */
    @Provides
    @Singleton
    fun provideActivityDao(database: FlyAwayDatabase): ActivityDao = database.activityDao()
}