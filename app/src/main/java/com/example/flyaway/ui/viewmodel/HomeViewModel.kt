package com.example.flyaway.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.data.repository.AuthRepository
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.domain.repository.TripRepository
import com.example.flyaway.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val getTripsUseCase: GetTripsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
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
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = authRepository.getCurrentUser()?.uid ?: throw Exception("Usuario no autenticado")
                Log.d("HomeViewModel", "Cargando viajes para usuario: $userId")
                getTripsUseCase(userId)
                    .catch { e ->
                        Log.e("HomeViewModel", "Error al cargar viajes: ${e.message}")
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                    .collect { trips ->
                        Log.d("HomeViewModel", "Viajes cargados: ${trips.size}")
                        _state.update { it.copy(trips = trips, isLoading = false) }
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener usuario: ${e.message}")
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun refresh() {
        loadTrips()
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val trips: List<Trip>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
} 