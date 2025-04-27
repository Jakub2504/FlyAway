package com.example.flyaway.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flyaway.data.local.converter.Converters
import com.example.flyaway.data.local.dao.AccessLogDao
import com.example.flyaway.data.local.dao.UserDao
import com.example.flyaway.data.local.entity.AccessLogEntity
import com.example.flyaway.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AccessLogEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
} 