package com.example.flyaway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flyaway.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM Reservation")
    fun getAllReservations(): Flow<List<Reservation>>

    @Query("SELECT * FROM Reservation WHERE id = :reservationId")
    suspend fun getReservationById(reservationId: String): Reservation?

    @Query("DELETE FROM Reservation WHERE id = :reservationId")
    suspend fun deleteReservationById(reservationId: String)
}