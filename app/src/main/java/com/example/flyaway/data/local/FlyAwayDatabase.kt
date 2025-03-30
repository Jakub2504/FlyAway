package com.example.flyaway.data.local

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.flyaway.data.local.dao.ActivityDao
import com.example.flyaway.data.local.dao.DayDao
import com.example.flyaway.data.local.dao.TripDao
import com.example.flyaway.data.local.entity.ActivityEntity
import com.example.flyaway.data.local.entity.DayEntity
import com.example.flyaway.data.local.entity.TripEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [TripEntity::class, DayEntity::class, ActivityEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlyAwayDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun activityDao(): ActivityDao
}

class Converters {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return try {
            value?.let { LocalDate.ofEpochDay(it) }
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir timestamp a LocalDate", e)
            null
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return try {
            date?.toEpochDay()
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir LocalDate a timestamp", e)
            null
        }
    }

    @TypeConverter
    fun fromTimeString(value: String?): LocalTime? {
        return try {
            value?.let { LocalTime.parse(it, timeFormatter) }
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir string a LocalTime", e)
            null
        }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return try {
            time?.format(timeFormatter)
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir LocalTime a string", e)
            null
        }
    }

    // Métodos auxiliares para formatear fechas
    fun formatDate(date: LocalDate?): String {
        return date?.format(dateFormatter) ?: ""
    }

    fun formatTime(time: LocalTime?): String {
        return time?.format(timeFormatter) ?: ""
    }
} 