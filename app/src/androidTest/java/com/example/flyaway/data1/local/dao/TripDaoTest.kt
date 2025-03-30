package com.example.flyaway.data1.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flyaway.data.local.dao.TripDao
import com.example.flyaway.data.local.FlyAwayDatabase
import com.example.flyaway.data.local.entity.TripEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TripDaoTest {
    private lateinit var database: FlyAwayDatabase
    private lateinit var tripDao: TripDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FlyAwayDatabase::class.java
        ).build()
        tripDao = database.tripDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndGetTrip() = runBlocking {
        val trip = TripEntity(
            id = "1",
            name = "Test Trip",
            destination = "Test Destination",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            createdAt = LocalDate.now()
        )

        tripDao.insertTrip(trip)

        val retrievedTrip = tripDao.getTripById("1").first()
        assertEquals(trip, retrievedTrip)
    }

    @Test
    fun deleteTrip() = runBlocking {
        val trip = TripEntity(
            id = "1",
            name = "Test Trip",
            destination = "Test Destination",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            createdAt = LocalDate.now()
        )

        tripDao.insertTrip(trip)
        tripDao.deleteTripById("1")

        val retrievedTrip = tripDao.getTripById("1").first()
        assertNull(retrievedTrip)
    }

    @Test
    fun getAllTrips() = runBlocking {
        val trip1 = TripEntity(
            id = "1",
            name = "Test Trip 1",
            destination = "Test Destination 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            createdAt = LocalDate.now()
        )

        val trip2 = TripEntity(
            id = "2",
            name = "Test Trip 2",
            destination = "Test Destination 2",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            createdAt = LocalDate.now()
        )

        tripDao.insertTrip(trip1)
        tripDao.insertTrip(trip2)

        val allTrips = tripDao.getAllTrips().first()
        assertEquals(2, allTrips.size)
        assertEquals(listOf(trip1, trip2), allTrips)
    }
} 