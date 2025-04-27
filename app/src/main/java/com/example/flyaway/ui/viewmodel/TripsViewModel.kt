package com.example.flyaway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripsState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TripsEvent {
    data object LoadTrips : TripsEvent()
    data class DeleteTrip(val tripId: String) : TripsEvent()
}

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripsState())
    val state: StateFlow<TripsState> = _state.asStateFlow()

    init {
        loadTrips()
    }

    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.LoadTrips -> loadTrips()
            is TripsEvent.DeleteTrip -> deleteTrip(event.tripId)
        }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.getAllTrips()
                    .collect { trips ->
                        _state.update { it.copy(trips = trips, isLoading = false) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                tripRepository.deleteTrip(tripId)
                loadTrips()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
} 