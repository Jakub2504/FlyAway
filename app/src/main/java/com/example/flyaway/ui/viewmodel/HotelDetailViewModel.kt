package com.example.flyaway.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.ReserveRequest
import com.example.flyaway.domain.model.Room
import com.example.flyaway.domain.repository.HotelRepository
import com.example.flyaway.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    internal val repo: HotelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    val uiState: StateFlow<HotelDetailUiState> = _uiState

    var showImageDialog by mutableStateOf(false)
    fun showImageDialog() { showImageDialog = true }
    fun hideImageDialog() { showImageDialog = false }

    fun selectRoom(room: Room) {
        _uiState.value = _uiState.value.copy(selectedRoom = room)
    }

    private lateinit var groupId: String
    private lateinit var start: String
    private lateinit var end: String

    /* -------- load hotel & free rooms -------- */
    fun loadRooms(hotelId: String, groupId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true)

                // Obtén la lista de hoteles
                val hotels = repo.getHotels(groupId)

                // Filtra el hotel correspondiente al hotelId
                val hotel = hotels.find { it.id == hotelId }

                // Extrae las habitaciones del hotel
                val rooms = hotel?.rooms ?: emptyList()

                _uiState.value = _uiState.value.copy(loading = false, rooms = rooms)
            } catch (e: Exception) {
                Log.e("HotelDetailViewModel", "Error al cargar habitaciones: ${e.message}")
                _uiState.value = _uiState.value.copy(loading = false, rooms = emptyList())
            }
        }
    }


//    fun reserveRoom(selectedRoom: Room) {
//        Log.d("hoteldetail", "room: $selectedRoom")
//        Log.i("hoteldetail", "room: $selectedRoom")
//        Log.d("hoteldetail", "room: $selectedRoom")
//    }

    /* -------- reserve selected room -------- */
    fun reserveRoom(room: Room) = viewModelScope.launch {

        Log.d("reserveRoom called", "room: $room")

        val req = ReserveRequest(
            hotelId = uiState.value.hotel!!.id,
            roomId  = room.id,
            startDate = start,
            endDate   = end,
            guestName = "Vitor", //CAMBIAR AQUI PARA TU USUARIO
            guestEmail = "vitorlui@gmail.com" //CAMBIAR AQUI PARA TU USUARIO
        )

        try {
            repo.reserve(groupId, req)   // we ignore response here
        } catch (e: HttpException) {
            val decodedError = ErrorUtils.extractErrorMessage(e)
            Log.e("BookViewModel", "HTTP error: ${decodedError}  $e")

        } catch (e: Exception) {
            Log.e("BookViewModel", "Error: ${e.localizedMessage}")
        }

    }
}


//try {
//    val hotels = repo.getAvailability(groupId, s.format(fmt), e.format(fmt), city = city)
//    _uiState.update { it.copy(loading = false, hotels = hotels) }
//} catch (e: HttpException) {
//
//    val decodedError = ErrorUtils.extractErrorMessage(e)
//
//    Log.e("BookViewModel", "HTTP error: ${decodedError}  $e")
//    _uiState.update { it.copy(loading = false, hotels = emptyList(), message = decodedError) }
//
//    _uiState.update {
//        it.copy(
//            loading = false,
//            hotels = emptyList(),
//            message = "Error: ${decodedError}}"
//        )
//    }
//
//} catch (e: Exception) {
//    Log.e("BookViewModel", "Error: ${e.localizedMessage}")
////            _uiState.update { it.copy(loading = false, hotels = emptyList()) }
//    _uiState.update {
//        it.copy(
//            loading = false,
//            hotels = emptyList(),
//            message = "Error: ${e.message}}"
//        )
//    }
//}

data class HotelDetailUiState(
    val loading: Boolean = true,
    val hotel: Hotel? = null,
    val rooms: List<Room>? = emptyList(),
    val selectedRoom: Room? = null,
    val showImageDialog: Boolean = false,
)