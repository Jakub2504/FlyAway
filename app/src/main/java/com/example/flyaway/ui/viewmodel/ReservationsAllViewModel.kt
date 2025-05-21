package com.example.flyaway.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Reservation
import com.example.flyaway.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationsAllViewModel @Inject constructor(
    private val repo: HotelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Map<String, List<Reservation>>>(emptyMap())
    val uiState: StateFlow<Map<String, List<Reservation>>> = _uiState

    fun load() = viewModelScope.launch {
        _uiState.value = repo.getAllReservations()
    }

    fun cancel(r: Reservation) = viewModelScope.launch {
        Log.d("viewmodel", "canceling: ${r.id}")
        repo.cancelById(r.id)
        load()
    }
}