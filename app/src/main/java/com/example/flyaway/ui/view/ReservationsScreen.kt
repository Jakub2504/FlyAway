package com.example.flyaway.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flyaway.ui.transitions.components.ReservationRow
import com.example.flyaway.ui.viewmodel.ReservationsViewModel

// ---------------------------- Reservations Screen ---------------------------
@Composable
fun ReservationsScreen(vm: ReservationsViewModel = hiltViewModel()) {
    val ui = vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()   // respeta notch y barra de navegación
            .padding(16.dp)

    ) {

        /* ---------- título global ---------- */
        item {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {


                Text(
                    text = "My Reservations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.statusBarsPadding().padding(start = 16.dp, bottom = 12.dp)
                )

                /*  refresh icon  */
                IconButton(onClick = { vm.load() }) {       // ← recarga la lista
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload"
                    )
                }

            }
        }

        items(ui.value.reservations) { r ->
            ReservationRow(
                res = r,
                onCancel = { vm.cancel(r) }
            )
        }
    }
}