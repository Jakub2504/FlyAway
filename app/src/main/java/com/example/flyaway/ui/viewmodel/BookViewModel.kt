package com.example.flyaway.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.BuildConfig
import com.example.flyaway.data.local.AppDatabase
import com.example.flyaway.data.local.entity.TripEntity
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.HotelRepository
import com.example.flyaway.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import retrofit2.HttpException

@HiltViewModel
class BookViewModel @Inject constructor(
    val hotelRepository: HotelRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState

    private val _tripState = MutableStateFlow(BookTripState())
    val tripState: StateFlow<BookTripState> = _tripState

    init {
        loadAllHotels()
    }

    fun loadAllHotels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            try {
                val hotels = hotelRepository.getHotels(groupId = BuildConfig.GROUP_ID)
                _uiState.value = _uiState.value.copy(hotels = hotels, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    message = "Error al cargar hoteles: ${e.message}"
                )
            }
        }
    }

    fun selectCity(city: String) {
        _uiState.update { currentState ->
            currentState.copy(city = city)
        }
        Log.d("BookViewModel", "Ciudad seleccionada: ${_uiState.value.city}")
    }

    fun selectStartDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun selectEndDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }

    fun search(city: String, startDate: LocalDate?, endDate: LocalDate?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            try {
                val hotels = if (startDate == null && endDate == null && city.isNotEmpty()) {
                    hotelRepository.getHotels(BuildConfig.GROUP_ID).filter { it.address?.contains(city, ignoreCase = true) == true }
                } else {
                    hotelRepository.getAvailability(
                        groupId = BuildConfig.GROUP_ID,
                        start = startDate?.format(DateTimeFormatter.ISO_DATE) ?: "",
                        end = endDate?.format(DateTimeFormatter.ISO_DATE) ?: "",
                        city = city.takeIf { it.isNotEmpty() } ?: ""
                    )
                }

                _uiState.value = _uiState.value.copy(
                    hotels = hotels,
                    loading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    message = "Error al buscar hoteles: ${e.message}"
                )
            }
        }
    }

    fun searchWithFilters(startDate: LocalDate?, endDate: LocalDate?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            try {
                val hotels = hotelRepository.getAvailability(
                    groupId = "defaultGroup",
                    start = startDate?.toString() ?: "",
                    end = endDate?.toString() ?: "",
                    city = _uiState.value.city.takeIf { it.isNotEmpty() }
                )
                _uiState.value = _uiState.value.copy(hotels = hotels, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    message = "Error al buscar hoteles: ${e.message}"
                )
            }
        }
    }

    fun saveReservationAsTrip(
        tripName: String,
        destination: String,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String,
        onTripCreated: (String) -> Unit // Callback para navegar
    ) {
        viewModelScope.launch {
            try {
                val tripId = UUID.randomUUID().toString()
                val tripEntity = TripEntity(
                    id = tripId,
                    userId = userId,
                    name = tripName,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    createdAt = LocalDate.now()
                )
                database.tripDao().insertTrip(tripEntity)
                Log.d("BookViewModel", "Reserva guardada como viaje: $tripEntity")
                onTripCreated(tripId) // Llamar al callback con el ID del viaje
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error al guardar la reserva: ${e.message}")
            }
        }
    }

    private fun generateDays(startDate: LocalDate, endDate: LocalDate): List<com.example.flyaway.domain.model.Day> {
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        return (0 until daysBetween).map { dayOffset ->
            val date = startDate.plusDays(dayOffset.toLong())
            com.example.flyaway.domain.model.Day(
                id = UUID.randomUUID().toString(),
                tripId = "", // El tripId se asignará en el repositorio
                date = date,
                dayNumber = dayOffset + 1
            )
        }
    }

    fun onTripEvent(event: BookTripEvent) {
        when (event) {
            is BookTripEvent.OnNameChange -> {
                _tripState.update { it.copy(
                    name = event.name,
                    nameError = validateName(event.name)
                ) }
            }
            is BookTripEvent.OnDestinationChange -> {
                _tripState.update { it.copy(
                    destination = event.destination,
                    destinationError = validateDestination(event.destination)
                ) }
            }
            is BookTripEvent.OnStartDateChange -> {
                val currentState = _tripState.value
                val endDate = currentState.endDate
                val dateError = if (endDate != null && event.date.isAfter(endDate)) {
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
                } else null
                _tripState.update { it.copy(startDate = event.date, dateError = dateError) }
            }
            is BookTripEvent.OnEndDateChange -> {
                val currentState = _tripState.value
                val startDate = currentState.startDate
                val dateError = if (startDate != null && event.date.isBefore(startDate)) {
                    "La fecha de fin no puede ser anterior a la fecha de inicio"
                } else null
                _tripState.update { it.copy(endDate = event.date, dateError = dateError) }
            }
            is BookTripEvent.OnCreateTrip -> {
                createTrip()
            }
        }
    }

    private fun createTrip() {
        val currentState = _tripState.value
        viewModelScope.launch {
            _tripState.update { it.copy(isLoading = true, error = null) }
            try {
                val tripEntity = TripEntity(
                    id = UUID.randomUUID().toString(),
                    userId = "userId", // Reemplaza con el ID real del usuario
                    name = currentState.name.trim(),
                    destination = currentState.destination.trim(),
                    startDate = currentState.startDate!!,
                    endDate = currentState.endDate!!,
                    createdAt = LocalDate.now()
                )
                database.tripDao().insertTrip(tripEntity)
                _tripState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _tripState.update { it.copy(isLoading = false, error = e.message ?: "Error al crear el viaje") }
            }
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre es obligatorio"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            name.length > 50 -> "El nombre no puede tener más de 50 caracteres"
            else -> null
        }
    }

    private fun validateDestination(destination: String): String? {
        return when {
            destination.isBlank() -> "El destino es obligatorio"
            destination.length < 3 -> "El destino debe tener al menos 3 caracteres"
            destination.length > 50 -> "El destino no puede tener más de 50 caracteres"
            else -> null
        }
    }
}

data class BookTripState(
    val name: String = "",
    val destination: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val nameError: String? = null,
    val destinationError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed class BookTripEvent {
    data class OnNameChange(val name: String) : BookTripEvent()
    data class OnDestinationChange(val destination: String) : BookTripEvent()
    data class OnStartDateChange(val date: LocalDate) : BookTripEvent()
    data class OnEndDateChange(val date: LocalDate) : BookTripEvent()
    data object OnCreateTrip : BookTripEvent()
}


data class BookUiState(
    val loading: Boolean = false,
    val cityMenu: Boolean = false,
    val city: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val hotels: List<Hotel> = emptyList(),
    val message: String? = null
)