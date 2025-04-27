package com.example.flyaway.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.flyaway.R
import com.example.flyaway.ui.transitions.components.ErrorScreen
import com.example.flyaway.ui.transitions.components.LoadingScreen
import com.example.flyaway.domain.model.Trip
import com.example.flyaway.ui.viewmodel.HomeEvent
import com.example.flyaway.ui.viewmodel.HomeState
import com.example.flyaway.ui.viewmodel.HomeViewModel
import com.example.flyaway.ui.navigation.AppScreens
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.runtime.livedata.observeAsState

/**
 * Pantalla principal de la aplicación.
 * Muestra la lista de viajes y permite crear uno nuevo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToCreateTrip: () -> Unit,
    onNavigateToTripDetails: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSettingsMenu by remember { mutableStateOf(false) }

    // Observar cuando se crea un nuevo viaje
    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.LoadTrips)
    }

    val tripCreated = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("trip_created")
        ?.observeAsState()
    if (tripCreated?.value == true) {
        viewModel.onEvent(HomeEvent.LoadTrips)
        navController.currentBackStackEntry?.savedStateHandle?.set("trip_created", false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(HomeEvent.LoadTrips)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeContent(
        state = state,
        drawerState = drawerState,
        showSettingsMenu = showSettingsMenu,
        onShowSettingsMenu = { showSettingsMenu = it },
        onOpenDrawer = { scope.launch { drawerState.open() } },
        onCloseDrawer = { scope.launch { drawerState.close() } },
        onNavigateToCreateTrip = onNavigateToCreateTrip,
        onNavigateToTripDetails = onNavigateToTripDetails,
        onNavigateToSettings = {
            showSettingsMenu = false
            onNavigateToSettings()
        },
        onNavigateToAboutUs = {
            scope.launch { drawerState.close() }
            onNavigateToAboutUs()
        },
        onNavigateToTerms = {
            scope.launch { drawerState.close() }
            onNavigateToTerms()
        },
        onTripClick = { tripId ->
            viewModel.onEvent(HomeEvent.TripSelected(tripId))
            onNavigateToTripDetails(tripId)
        },
        onRefresh = { viewModel.onEvent(HomeEvent.LoadTrips) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeState,
    drawerState: DrawerState,
    showSettingsMenu: Boolean,
    onShowSettingsMenu: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onNavigateToCreateTrip: () -> Unit,
    onNavigateToTripDetails: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onTripClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                onNavigateToAboutUs = onNavigateToAboutUs,
                onNavigateToTerms = onNavigateToTerms,
                onClose = onCloseDrawer
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(
                                onClick = { onShowSettingsMenu(true) }
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.settings)
                                )
                            }

                            DropdownMenu(
                                expanded = showSettingsMenu,
                                onDismissRequest = { onShowSettingsMenu(false) }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.settings)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Settings, contentDescription = null)
                                    },
                                    onClick = onNavigateToSettings
                                )

                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.profile)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    onClick = {
                                        onShowSettingsMenu(false)
                                        // Aquí debería ir la navegación al perfil, pero como no la tenemos como
                                        // parámetro, usamos la navegación a configuración
                                        onNavigateToSettings()
                                    }
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToCreateTrip,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.create_trip)
                    )
                }
            }
        ) { padding ->
            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(message = state.error ?: "Unknown error")
                else -> {
                    if (state.trips.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Flight,
                                contentDescription = null,
                                modifier = Modifier.Companion.size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.no_trips_yet),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.create_trip_prompt),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(R.string.your_trips),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            items(state.trips) { trip ->
                                TripItem(
                                    trip = trip,
                                    onClick = { onTripClick(trip.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppDrawerContent(
    onNavigateToAboutUs: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Flight,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            DrawerItem(
                icon = Icons.Default.Home,
                label = stringResource(R.string.nav_home),
                onClick = onClose,
                selected = true
            )

            DrawerItem(
                icon = Icons.Default.Info,
                label = stringResource(R.string.nav_about),
                onClick = onNavigateToAboutUs
            )

            DrawerItem(
                icon = Icons.Default.Description,
                label = stringResource(R.string.nav_terms),
                onClick = onNavigateToTerms
            )

            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.close))
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.Companion.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripItem(
    trip: Trip,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${trip.startDate.format(dateFormatter)} - ${
                        trip.endDate.format(
                            dateFormatter
                        )
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Calcular la duración del viaje en lugar de contar los días
                val duration = ChronoUnit.DAYS.between(trip.startDate, trip.endDate) + 1
                Text(
                    text = stringResource(
                        id = R.string.days_count,
                        formatArgs = arrayOf(duration)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}