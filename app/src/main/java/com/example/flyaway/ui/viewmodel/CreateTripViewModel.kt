package com.example.flyaway.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.data.repository.AuthRepository
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import com.example.flyaway.domain.usecase.SaveTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import android.util.Log

/**
 * Estado para la pantalla de creación de viaje
 */
data class CreateTripState(
    val name: String = "",
    val destination: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val nameError: String? = null,
    val destinationError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,
    val tripCreated: Boolean = false
)

/**
 * Eventos que pueden ocurrir en la pantalla de creación de viaje
 */
sealed class CreateTripEvent {
    data class OnNameChange(val name: String) : CreateTripEvent()
    data class OnDestinationChange(val destination: String) : CreateTripEvent()
    data class OnStartDateChange(val date: LocalDate) : CreateTripEvent()
    data class OnEndDateChange(val date: LocalDate) : CreateTripEvent()
    data object OnStartDateClick : CreateTripEvent()
    data object OnEndDateClick : CreateTripEvent()
    data object OnDatePickerDismiss : CreateTripEvent()
    data class OnDateSelected(val date: LocalDate, val isStartDate: Boolean) : CreateTripEvent()
    data object OnCreateTrip : CreateTripEvent()
    data object OnResetState : CreateTripEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CreateTripViewModel @Inject constructor(
    private val saveTripUseCase: SaveTripUseCase,
    private val authRepository: AuthRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateTripState())
    val state: StateFlow<CreateTripState> = _state.asStateFlow()

    fun onEvent(event: CreateTripEvent) {
        when (event) {
            is CreateTripEvent.OnNameChange -> {
                _state.update { it.copy(
                    name = event.name,
                    nameError = validateName(event.name)
                ) }
            }
            is CreateTripEvent.OnDestinationChange -> {
                _state.update { it.copy(
                    destination = event.destination,
                    destinationError = validateDestination(event.destination)
                ) }
            }
            is CreateTripEvent.OnStartDateChange -> {
                val currentState = _state.value
                val endDate = currentState.endDate
                
                // Si la fecha de fin está establecida, valida que la fecha de inicio no sea posterior
                val dateError = if (endDate != null && event.date.isAfter(endDate)) {
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
                } else {
                    null
                }
                
                _state.update { it.copy(
                    startDate = event.date,
                    dateError = dateError
                ) }
            }
            is CreateTripEvent.OnEndDateChange -> {
                val currentState = _state.value
                val startDate = currentState.startDate
                
                // Si la fecha de inicio está establecida, valida que la fecha de fin no sea anterior
                val dateError = if (startDate != null && event.date.isBefore(startDate)) {
                    "La fecha de fin no puede ser anterior a la fecha de inicio"
                } else {
                    null
                }
                
                _state.update { it.copy(
                    endDate = event.date,
                    dateError = dateError
                ) }
            }
            is CreateTripEvent.OnStartDateClick -> {
                _state.update { it.copy(showStartDatePicker = true) }
            }
            is CreateTripEvent.OnEndDateClick -> {
                _state.update { it.copy(showEndDatePicker = true) }
            }
            is CreateTripEvent.OnDatePickerDismiss -> {
                _state.update { it.copy(
                    showStartDatePicker = false,
                    showEndDatePicker = false
                ) }
            }
            is CreateTripEvent.OnDateSelected -> {
                if (event.isStartDate) {
                    onEvent(CreateTripEvent.OnStartDateChange(event.date))
                    _state.update { it.copy(showStartDatePicker = false) }
                } else {
                    onEvent(CreateTripEvent.OnEndDateChange(event.date))
                    _state.update { it.copy(showEndDatePicker = false) }
                }
            }
            is CreateTripEvent.OnCreateTrip -> {
                createTrip()
            }
            is CreateTripEvent.OnResetState -> {
                _state.update { CreateTripState() }
            }
        }
    }

    private fun createTrip() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = authRepository.getCurrentUser()?.uid ?: throw Exception("Usuario no autenticado")
                // Generar los días del viaje según el rango de fechas
                val start = currentState.startDate!!
                val end = currentState.endDate!!
                val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt() + 1
                val days = (0 until daysBetween).map { dayOffset ->
                    val date = start.plusDays(dayOffset.toLong())
                    com.example.flyaway.domain.model.Day(
                        id = UUID.randomUUID().toString(),
                        tripId = "", // El tripId se asignará en el repositorio
                        date = date,
                        dayNumber = dayOffset + 1
                    )
                }
                val trip = Trip(
                    id = UUID.randomUUID().toString(),
                    name = currentState.name.trim(),
                    destination = currentState.destination.trim(),
                    startDate = start,
                    endDate = end,
                    createdAt = java.time.LocalDate.now(),
                    days = days
                )
                val savedTrip = saveTripUseCase(trip, userId)
                _state.update { it.copy(isLoading = false, isSuccess = true, tripCreated = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al crear el viaje") }
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

    private fun validateDates(startDate: LocalDate?, endDate: LocalDate?): String? {
        return when {
            startDate == null -> "La fecha de inicio es obligatoria"
            endDate == null -> "La fecha de fin es obligatoria"
            startDate.isAfter(endDate) -> "La fecha de inicio no puede ser posterior a la fecha de fin"
            else -> null
        }
    }

    fun resetState() {
        _state.update { it.copy(tripCreated = false) }
    }
} 