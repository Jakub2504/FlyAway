package com.example.flyaway.data.repository

import android.util.Log
import com.example.flyaway.data.local.dao.ActivityDao
import com.example.flyaway.data.local.dao.DayDao
import com.example.flyaway.data.local.dao.TripDao
import com.example.flyaway.data.local.entity.ActivityEntity
import com.example.flyaway.data.local.entity.DayEntity
import com.example.flyaway.data.local.entity.TripEntity
import com.example.flyaway.domain.model.Activity
import com.example.flyaway.domain.model.Day
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de viajes.
 * Esta implementación usa Room Database para almacenar los datos de manera persistente.
 */
@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val dayDao: DayDao,
    private val activityDao: ActivityDao
) : TripRepository {

    override fun getAllTrips(userId: String): Flow<List<Trip>> {
        Log.d("TripRepositoryImpl", "Obteniendo todos los viajes para usuario: $userId")
        return tripDao.getAllTripsByUserId(userId).map { tripEntities ->
            Log.d("TripRepositoryImpl", "Entidades de viajes obtenidas: ${tripEntities.size}")
            tripEntities.map { tripEntity ->
                // Esta función carga solo los datos básicos del viaje
                // Los días y actividades se cargarán bajo demanda
                tripEntity.toDomainModel()
            }
        }
    }

    override fun getTripById(tripId: String, userId: String): Flow<Trip?> {
        return tripDao.getTripById(tripId, userId).map { tripEntity ->
            tripEntity?.let {
                // Cuando solicitamos un viaje específico, cargamos los días
                // y actividades asociadas
                val days = dayDao.getDaysByTripId(it.id).first().map { dayEntity ->
                    val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                        .map { activityEntity -> activityEntity.toDomainModel() }
                    dayEntity.toDomainModel().copy(activities = activities)
                }
                it.toDomainModel().copy(days = days)
            }
        }
    }

    override suspend fun saveTrip(trip: Trip, userId: String): Trip {
        Log.d("TripRepositoryImpl", "Guardando viaje: ${trip.id}, usuario: $userId, nombre: ${trip.name}, destino: ${trip.destination}")
        try {
            tripDao.insertTrip(trip.toEntity(userId))
            trip.days.forEach { day ->
                dayDao.insertDay(day.toEntity(trip.id))
                day.activities.forEach { activity ->
                    activityDao.insertActivity(activity.toEntity(day.id))
                }
            }
            Log.d("TripRepositoryImpl", "Viaje guardado correctamente: ${trip.id}")
            return trip
        } catch (e: Exception) {
            Log.e("TripRepositoryImpl", "Error al guardar el viaje: ${e.message}")
            throw e
        }
    }
    
    override suspend fun deleteTrip(tripId: String, userId: String) {
        tripDao.deleteTripById(tripId, userId)
    }

    override suspend fun createInitialDaysForTrip(trip: Trip, userId: String): Trip {
        val daysBetween = ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
        val days = (0 until daysBetween).map { dayOffset ->
            val date = trip.startDate.plusDays(dayOffset.toLong())
            Day(
                tripId = trip.id,
                date = date,
                dayNumber = dayOffset + 1  // Esto asegura que los días estén numerados correctamente desde el principio
            )
        }
        
        // Crear un nuevo viaje con los días
        val updatedTrip = trip.copy(days = days)
        
        // Guardar el viaje actualizado
        return saveTrip(updatedTrip, userId)
    }
    
    override suspend fun saveDay(day: Day, userId: String): Trip? {
        val trip = getTripById(day.tripId, userId).first() ?: return null

        // Primero, insertar o actualizar el día
        dayDao.insertDay(day.toEntity(trip.id))

        // Obtener todos los días actualizados para este viaje
        val allDays = dayDao.getDaysByTripId(trip.id).first()

        // Ordenar los días por fecha y recalcular los números de día
        val sortedDays = allDays.sortedBy { it.date }
            .mapIndexed { index, dayEntity ->
                // Si el número de día es diferente, actualizarlo
                if (dayEntity.dayNumber != index + 1) {
                    val updatedDay = dayEntity.copy(dayNumber = index + 1)
                    dayDao.updateDay(updatedDay)
                    updatedDay
        } else {
                    dayEntity
                }
        }
        
        // Convertir a modelos de dominio y cargar las actividades
        val domainDays = sortedDays.map { dayEntity ->
            val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                .map { it.toDomainModel() }
            dayEntity.toDomainModel().copy(activities = activities)
        }
        
        // Actualizar el viaje con los días actualizados
        val updatedTrip = trip.copy(days = domainDays)
        tripDao.updateTrip(updatedTrip.toEntity(userId))
        
        return updatedTrip
    }
    
    override suspend fun deleteDay(tripId: String, dayId: String, userId: String): Trip? {
        val trip = getTripById(tripId, userId).first() ?: return null

        // Eliminar el día
        dayDao.deleteDayById(dayId)

        // Obtener todos los días restantes para este viaje
        val remainingDays = dayDao.getDaysByTripId(tripId).first()

        // Si no quedan días, simplemente devolver el viaje actualizado
        if (remainingDays.isEmpty()) {
            val updatedTrip = trip.copy(days = emptyList())
            tripDao.updateTrip(updatedTrip.toEntity(userId))
            return updatedTrip
        }

        // Ordenar los días por fecha y renumerarlos
        val renumberedDays = remainingDays.sortedBy { it.date }
            .mapIndexed { index, dayEntity ->
                val updatedDay = dayEntity.copy(dayNumber = index + 1)
                // Solo actualizar si el número cambió
                if (updatedDay.dayNumber != dayEntity.dayNumber) {
                    dayDao.updateDay(updatedDay)
                }
                updatedDay
            }

        // Convertir a modelos de dominio y cargar las actividades
        val domainDays = renumberedDays.map { dayEntity ->
            val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                .map { it.toDomainModel() }
            dayEntity.toDomainModel().copy(activities = activities)
        }

        // Actualizar el viaje con los días actualizados
        val updatedTrip = trip.copy(days = domainDays)
        tripDao.updateTrip(updatedTrip.toEntity(userId))

        return updatedTrip
    }

    override suspend fun saveActivity(activity: Activity, userId: String): Day? {
        val day = dayDao.getDayById(activity.dayId).first() ?: return null

        activityDao.insertActivity(activity.toEntity(day.id))

        // Obtener todas las actividades actualizadas para este día
        val updatedActivities = activityDao.getActivitiesByDayId(day.id).first()
            .map { it.toDomainModel() }
        
        // Actualizar el día con las nuevas actividades
        val updatedDay = day.toDomainModel().copy(activities = updatedActivities)
        
        // Guardar el día actualizado
        dayDao.updateDay(updatedDay.toEntity(updatedDay.tripId))
        
        return updatedDay
    }
    
    override suspend fun deleteActivity(dayId: String, activityId: String, userId: String): Day? {
        val day = dayDao.getDayById(dayId).first() ?: return null

        activityDao.deleteActivityById(activityId)

        // Obtener todas las actividades restantes para este día
        val remainingActivities = activityDao.getActivitiesByDayId(dayId).first()
            .map { it.toDomainModel() }
        
        // Actualizar el día con las actividades actualizadas
        val updatedDay = day.toDomainModel().copy(activities = remainingActivities)
        
        // Guardar el día actualizado
        dayDao.updateDay(updatedDay.toEntity(updatedDay.tripId))
        
        return updatedDay
    }

    private fun TripEntity.toDomainModel(): Trip {
        // Crear un viaje sin días inicialmente para optimizar el rendimiento
        // Los días se cargarán bajo demanda en getTripById
        return Trip(
            id = id,
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            createdAt = createdAt,
            days = emptyList()
        )
    }

    private fun Trip.toEntity(userId: String): TripEntity {
        return TripEntity(
            id = id,
            userId = userId,
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            createdAt = createdAt
        )
    }

    private fun DayEntity.toDomainModel(): Day {
        return Day(
            id = id,
            tripId = tripId,
            date = date,
            dayNumber = dayNumber
        )
    }

    private fun Day.toEntity(tripId: String): DayEntity {
        return DayEntity(
            id = id,
            tripId = tripId,
            date = date,
            dayNumber = dayNumber
        )
    }

    private fun ActivityEntity.toDomainModel(): Activity {
        return Activity(
            id = id,
            dayId = dayId,
            name = name,
            description = description,
            startTime = startTime,
            endTime = endTime,
            location = location
        )
    }

    private fun Activity.toEntity(dayId: String): ActivityEntity {
        return ActivityEntity(
            id = id,
            dayId = dayId,
            name = name,
            description = description,
            startTime = startTime,
            endTime = endTime,
            location = location
        )
    }
} 