package com.example.flyaway.ui.view

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.res.stringResource
import java.util.Calendar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.flyaway.BuildConfig
import com.example.flyaway.R
import com.example.flyaway.domain.model.Hotel
import com.example.flyaway.domain.repository.HotelRepository
import com.example.flyaway.ui.navigation.AppScreens
import com.example.flyaway.ui.view.Screen
import com.example.flyaway.ui.viewmodel.ReservationsAllViewModel
import com.example.flyaway.ui.viewmodel.BookViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ------------------------ Navigation Destinations ---------------------------
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Book : Screen("book", Icons.Default.Search, "Book")
    object MyRes : Screen("my_reservations", Icons.Default.ListAlt, "My Reservations")
    object AllRes : Screen("all_reservations", Icons.Default.AdminPanelSettings, "All Reservations")
    object Hotel : Screen("hotel/{hotelId}/{groupId}/{start}/{end}", Icons.Default.Hotel, "Hotel") {
        fun create(hid: String, gid: String, s: String, e: String) = "hotel/$hid/$gid/$s/$e"
    }
}

val base = BuildConfig.API_URL.trimEnd('/')

// ------------------------ HomeHotel Composable ---------------------------
@OptIn(ExperimentalMaterial3Api::class)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun HomeHotel(rootNav: NavController, bookViewModel: BookViewModel = hiltViewModel()) {
    val uiState by bookViewModel.uiState.collectAsState()

    val cities = listOf("Londres", "Barcelona", "Paris")
    var expanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search hotels") },
                navigationIcon = {
                    IconButton(onClick = { rootNav.navigate(AppScreens.HomeScreen.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.city.ifEmpty { "Select a city" },
                        onValueChange = {},
                        label = { Text("City") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { expanded = true },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select a city",
                                modifier = Modifier.clickable { expanded = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city) },
                                onClick = {
                                    bookViewModel.selectCity(city)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DatePickerField(
                        label = "Start date",
                        date = startDate,
                        onDateSelected = { selectedDate ->
                            startDate = selectedDate
                            bookViewModel.selectStartDate(selectedDate)
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DatePickerField(
                        label = "End date",
                        date = endDate,
                        onDateSelected = { selectedDate ->
                            endDate = selectedDate
                            bookViewModel.selectEndDate(selectedDate)
                        }
                    )
                }

                Button(
                    onClick = {
                        bookViewModel.search(
                            city = uiState.city,
                            startDate = startDate,
                            endDate = endDate
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Search")
                }

                // Lista de hoteles
                if (uiState.loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (uiState.hotels.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.hotels) { hotel ->
                            HotelItem(
                                hotel = hotel,
                                rootNav = rootNav,
                                startDate = startDate?.toString() ?: "",
                                endDate = endDate?.toString() ?: ""
                            )
                        }
                    }
                } else if (uiState.message != null) {
                    Text(
                        text = uiState.message ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    )
}
@Composable
fun DatePickerField(label: String, date: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = date?.format(formatter) ?: "",
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = stringResource(R.string.Open_calendar),
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        }
    )
}

@Composable
fun HotelItem(hotel: Hotel, rootNav: NavController, startDate: String, endDate: String) {
    val fullImageUrl = "${BuildConfig.API_URL.trimEnd('/')}${hotel.imageUrl}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(fullImageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = hotel.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = hotel.address, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Rating: ${hotel.rating}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    rootNav.navigate(
                        AppScreens.HotelDetailsScreen.createRoute(
                            hotelId = hotel.id,
                            startDate = startDate.ifEmpty { "defaultStartDate" },
                            endDate = endDate.ifEmpty { "defaultEndDate" }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.See_details))
            }
        }
    }
}



