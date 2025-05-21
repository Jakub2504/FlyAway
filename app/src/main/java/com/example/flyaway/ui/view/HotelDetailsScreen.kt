package com.example.flyaway.ui.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.items // Asegúrate de este import
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.flyaway.BuildConfig
import com.example.flyaway.R
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.model.Room
import com.example.flyaway.domain.repository.HotelRepository
import com.example.flyaway.ui.transitions.components.ErrorScreen
import com.example.flyaway.ui.transitions.components.LoadingScreen
import com.example.flyaway.ui.viewmodel.BookViewModel
import com.example.flyaway.ui.viewmodel.HotelDetailViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotelId: String,
    groupId: String = BuildConfig.GROUP_ID,
    startDate: String,
    endDate: String,
    hotelRepository: HotelRepository,
    bookViewModel: BookViewModel = hiltViewModel(), // Se inyecta el BookViewModel
    onNavigateBack: () -> Unit
) {
    val viewModel: HotelDetailViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadRooms(hotelId, groupId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.Hotel_details)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.Back)
                        )
                    }
                }
            )
        },
        content = { padding ->
            when {
                uiState.loading -> {
                    LoadingScreen()
                }
                uiState.rooms.isNullOrEmpty() -> {
                    ErrorScreen(
                        message = stringResource(R.string.Rooms_not_found),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.rooms) { room ->
                            RoomItem(
                                room = room,
                                isAvailable = true, // Cambia esta lógica según la disponibilidad real
                                onReserve = {
                                    // Lógica para manejar la reserva
                                    bookViewModel.saveReservationAsTrip(
                                        tripName = "Reserve in ${room.roomType}",
                                        destination = hotelId,
                                        startDate = LocalDate.parse(startDate),
                                        endDate = LocalDate.parse(endDate),
                                        userId = "userId",
                                        onTripCreated = {
                                            // Aquí puedes manejar la respuesta después de crear el viaje
                                            Log.d("HotelDetailsScreen", "Reserva creada con éxito: $it")
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun RoomItem(room: Room, isAvailable: Boolean, onReserve: () -> Unit) {
    val images = room.images.map { "${BuildConfig.API_URL.trimEnd('/')}$it" }
    val pagerState = rememberPagerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Carrusel de imágenes
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) { page ->
                Image(
                    painter = rememberAsyncImagePainter(images[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información de la habitación
            Text(
                text = "Type: ${room.roomType}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Price: ${room.price} €",
                style = MaterialTheme.typography.bodyMedium
            )

            if (isAvailable) {
                Text(
                    text = "Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onReserve) {
                    Text("Reserve")
                }
            } else {
                Text(
                    text = "Not available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}