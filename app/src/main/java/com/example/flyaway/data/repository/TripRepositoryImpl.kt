package com.example.flyaway.data.repository

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
import com.google.firebase.auth.FirebaseAuth
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
    private val activityDao: ActivityDao,
    private val firebaseAuth: FirebaseAuth
) : TripRepository {

    override fun getAllTrips(): Flow<List<Trip>> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            tripDao.getAllTrips().map { tripEntities ->
                tripEntities
                    .filter { it.userId == currentUser.uid }
                    .map { tripEntity ->
                        tripEntity.toDomainModel()
                    }
            }
        } else {
            tripDao.getAllTrips().map { emptyList() }
        }
    }

    override fun getTripById(tripId: String): Flow<Trip?> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            tripDao.getTripById(tripId).map { tripEntity ->
                tripEntity?.let {
                    if (it.userId == currentUser.uid) {
                        val days = dayDao.getDaysByTripId(it.id).first().map { dayEntity ->
                            val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                                .map { activityEntity -> activityEntity.toDomainModel() }
                            dayEntity.toDomainModel().copy(activities = activities)
                        }
                        it.toDomainModel().copy(days = days)
                    } else {
                        null
                    }
                }
            }
        } else {
            tripDao.getTripById(tripId).map { null }
        }
    }

    override suspend fun saveTrip(trip: Trip): Trip {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to save trips")
        }

        val tripWithUserId = trip.copy(userId = currentUser.uid)
        tripDao.insertTrip(tripWithUserId.toEntity())
        tripWithUserId.days.forEach { day ->
            dayDao.insertDay(day.toEntity(tripWithUserId.id))
            day.activities.forEach { activity ->
                activityDao.insertActivity(activity.toEntity(day.id))
            }
        }
        return tripWithUserId
    }

    override suspend fun deleteTrip(tripId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to delete trips")
        }

        val trip = tripDao.getTripById(tripId).first()
        if (trip?.userId == currentUser.uid) {
            tripDao.deleteTripById(tripId)
        } else {
            throw IllegalStateException("Cannot delete trip that doesn't belong to current user")
        }
    }

    override suspend fun createInitialDaysForTrip(trip: Trip): Trip {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to create trips")
        }

        val tripWithUserId = trip.copy(userId = currentUser.uid)
        val daysBetween = ChronoUnit.DAYS.between(tripWithUserId.startDate, tripWithUserId.endDate).toInt() + 1
        val days = (0 until daysBetween).map { dayOffset ->
            val date = tripWithUserId.startDate.plusDays(dayOffset.toLong())
            Day(
                tripId = tripWithUserId.id,
                date = date,
                dayNumber = dayOffset + 1
            )
        }

        val updatedTrip = tripWithUserId.copy(days = days)
        return saveTrip(updatedTrip)
    }

    override suspend fun saveDay(day: Day): Trip? {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to save days")
        }

        val trip = getTripById(day.tripId).first() ?: return null
        if (trip.userId != currentUser.uid) {
            throw IllegalStateException("Cannot save day for trip that doesn't belong to current user")
        }

        dayDao.insertDay(day.toEntity(trip.id))
        val allDays = dayDao.getDaysByTripId(trip.id).first()
        val sortedDays = allDays.sortedBy { it.date }
            .mapIndexed { index, dayEntity ->
                if (dayEntity.dayNumber != index + 1) {
                    val updatedDay = dayEntity.copy(dayNumber = index + 1)
                    dayDao.updateDay(updatedDay)
                    updatedDay
                } else {
                    dayEntity
                }
            }

        val domainDays = sortedDays.map { dayEntity ->
            val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                .map { it.toDomainModel() }
            dayEntity.toDomainModel().copy(activities = activities)
        }

        val updatedTrip = trip.copy(days = domainDays)
        tripDao.updateTrip(updatedTrip.toEntity())
        return updatedTrip
    }

    override suspend fun deleteDay(tripId: String, dayId: String): Trip? {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to delete days")
        }

        val trip = getTripById(tripId).first() ?: return null
        if (trip.userId != currentUser.uid) {
            throw IllegalStateException("Cannot delete day from trip that doesn't belong to current user")
        }

        dayDao.deleteDayById(dayId)
        val remainingDays = dayDao.getDaysByTripId(tripId).first()
        val sortedDays = remainingDays.sortedBy { it.date }
            .mapIndexed { index, dayEntity ->
                if (dayEntity.dayNumber != index + 1) {
                    val updatedDay = dayEntity.copy(dayNumber = index + 1)
                    dayDao.updateDay(updatedDay)
                    updatedDay
                } else {
                    dayEntity
                }
            }

        val domainDays = sortedDays.map { dayEntity ->
            val activities = activityDao.getActivitiesByDayId(dayEntity.id).first()
                .map { it.toDomainModel() }
            dayEntity.toDomainModel().copy(activities = activities)
        }

        val updatedTrip = trip.copy(days = domainDays)
        tripDao.updateTrip(updatedTrip.toEntity())
        return updatedTrip
    }

    override suspend fun saveActivity(activity: Activity): Day? {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to save activities")
        }

        val day = dayDao.getDayById(activity.dayId).first() ?: return null
        val trip = getTripById(day.tripId).first() ?: return null
        if (trip.userId != currentUser.uid) {
            throw IllegalStateException("Cannot save activity for trip that doesn't belong to current user")
        }

        activityDao.insertActivity(activity.toEntity(day.id))
        val activities = activityDao.getActivitiesByDayId(day.id).first()
            .map { it.toDomainModel() }
        val updatedDay = day.toDomainModel().copy(activities = activities)
        dayDao.updateDay(updatedDay.toEntity(day.tripId))
        return updatedDay
    }

    override suspend fun deleteActivity(dayId: String, activityId: String): Day? {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User must be logged in to delete activities")
        }

        val day = dayDao.getDayById(dayId).first() ?: return null
        val trip = getTripById(day.tripId).first() ?: return null
        if (trip.userId != currentUser.uid) {
            throw IllegalStateException("Cannot delete activity from trip that doesn't belong to current user")
        }

        activityDao.deleteActivityById(activityId)
        val remainingActivities = activityDao.getActivitiesByDayId(dayId).first()
            .map { it.toDomainModel() }
        val updatedDay = day.toDomainModel().copy(activities = remainingActivities)
        dayDao.updateDay(updatedDay.toEntity(day.tripId))
        return updatedDay
    }

    private fun TripEntity.toDomainModel(): Trip {
        return Trip(
            id = id,
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            createdAt = createdAt,
            days = emptyList(),
            userId = userId
        )
    }

    private fun Trip.toEntity(): TripEntity {
        return TripEntity(
            id = id,
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            createdAt = createdAt,
            userId = userId
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