package com.example.flyaway.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flyaway.data.local.converter.Converters
import com.example.flyaway.data.local.dao.*
import com.example.flyaway.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        UserEntity::class,
        AccessLogEntity::class,
        TripEntity::class, // Nueva entidad
        DayEntity::class,  // Nueva entidad
        ActivityEntity::class // Nueva entidad
    ],
    version = 8, // Incrementa la versión de la base de datos
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun tripDao(): TripDao // Nuevo DAO
    abstract fun dayDao(): DayDao   // Nuevo DAO
    abstract fun activityDao(): ActivityDao // Nuevo DAO
}