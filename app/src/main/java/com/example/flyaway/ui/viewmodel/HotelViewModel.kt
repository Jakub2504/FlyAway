package com.example.flyaway.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Reservation
import com.example.flyaway.domain.usecase.CancelHotelUseCase
import com.example.flyaway.domain.usecase.ReserveHotelUseCase
import com.example.flyaway.domain.usecase.SearchHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val searchHotelsUseCase: SearchHotelsUseCase,
    private val reserveHotelUseCase: ReserveHotelUseCase,
    private val cancelHotelUseCase: CancelHotelUseCase
) : ViewModel() {

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun search(city: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                hotels = searchHotelsUseCase(city, startDate, endDate)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun reserveRoom(reservation: Reservation) {
        viewModelScope.launch {
            try {
                val success = reserveHotelUseCase(reservation)
                if (success) {
                    // Actualizar el estado para indicar éxito
                    errorMessage = null
                    isLoading = false
                    // Aquí podrías emitir un evento para navegar
                    onReservationSuccess()
                } else {
                    errorMessage = "Error al reservar la habitación"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            try {
                val success = cancelHotelUseCase(reservationId)
                if (success) {
                    // Actualizar el estado para indicar éxito
                    errorMessage = null
                    isLoading = false
                    // Aquí podrías emitir un evento para navegar
                } else {
                    errorMessage = "Error al cancelar la reserva"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    private fun onReservationSuccess() {
        // Lógica para actualizar la UI o emitir un evento de navegación
        // Por ejemplo, podrías usar un LiveData o StateFlow para notificar a la vista
    }
}