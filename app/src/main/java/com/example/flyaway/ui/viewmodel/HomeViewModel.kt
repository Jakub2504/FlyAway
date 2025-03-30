package com.example.flyaway.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import com.example.flyaway.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la pantalla de inicio que contiene la lista de viajes
 */
data class HomeState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Eventos que pueden ocurrir en la pantalla de inicio
 */
sealed class HomeEvent {
    data object LoadTrips : HomeEvent()
    data class TripSelected(val tripId: String) : HomeEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val getTripsUseCase: GetTripsUseCase
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    
    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    
    // Estado combinado de la pantalla
    val state: StateFlow<HomeState> = combine(
        tripRepository.getAllTrips(),
        _isLoading,
        _error
    ) { trips, isLoading, error ->
        HomeState(
            trips = trips,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState(isLoading = true)
    )
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadTrips()
    }
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadTrips -> loadTrips()
            is HomeEvent.TripSelected -> {
                // Este evento sería manejado por la capa de UI para navegar
                // No necesitamos hacer nada aquí
            }
        }
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            try {
                getTripsUseCase().collect { trips ->
                    _uiState.value = HomeUiState.Success(trips)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading trips", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val trips: List<Trip>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
} 