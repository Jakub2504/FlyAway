package com.example.flyaway.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavScreen("home_screen", Icons.Default.Home, "Home")
    object HomeHotel : BottomNavScreen("home_hotel", Icons.Default.Hotel, "Hotels")
}