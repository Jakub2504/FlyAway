package com.example.flyaway.data.local

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.flyaway.data.local.dao.AccessLogDao
import com.example.flyaway.data.local.dao.ActivityDao
import com.example.flyaway.data.local.dao.DayDao
import com.example.flyaway.data.local.dao.TripDao
import com.example.flyaway.data.local.dao.UserDao
import com.example.flyaway.data.local.entity.AccessLogEntity
import com.example.flyaway.data.local.entity.ActivityEntity
import com.example.flyaway.data.local.entity.DayEntity
import com.example.flyaway.data.local.entity.TripEntity
import com.example.flyaway.data.local.entity.UserEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [
        TripEntity::class,
        DayEntity::class,
        ActivityEntity::class,
        UserEntity::class,
        AccessLogEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlyAwayDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
}

class Converters {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

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

    @TypeConverter
    fun fromDateTimeString(value: String?): LocalDateTime? {
        return try {
            value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir string a LocalDateTime", e)
            null
        }
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        return try {
            dateTime?.format(dateTimeFormatter)
        } catch (e: Exception) {
            Log.e("Converters", "Error al convertir LocalDateTime a string", e)
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

    fun formatDateTime(dateTime: LocalDateTime?): String {
        return dateTime?.format(dateTimeFormatter) ?: ""
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(json: String?): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}