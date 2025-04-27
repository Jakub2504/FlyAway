package com.example.flyaway.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.flyaway.ui.view.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.example.flyaway.ui.transitions.animations.*
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Navegación principal de la aplicación.
 * Configura todas las rutas y destinos para la navegación entre pantallas.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH_ROUTE
    ) {
        // Pantalla de Splash
        composable(
            route = AppDestinations.SPLASH_ROUTE,
            exitTransition = { splashExitTransition() }
        ) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Login
        composable(
            route = AppDestinations.LOGIN_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            LoginScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        // Pantalla de Registro
        composable(
            route = AppDestinations.REGISTER_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            RegisterScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        // Pantalla de Recuperación de Contraseña
        composable(
            route = AppDestinations.RESET_PASSWORD_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            ResetPasswordScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.RESET_PASSWORD_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Viajes
        composable(
            route = AppDestinations.TRIPS_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            TripsScreen(
                onNavigateToCreateTrip = {
                    navController.navigate(AppDestinations.CREATE_TRIP_ROUTE)
                },
                onNavigateToTripDetails = { tripId ->
                    navController.navigate("${AppDestinations.TRIP_DETAILS_ROUTE}/$tripId")
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestinations.SETTINGS_ROUTE)
                }
            )
        }
        
        // Pantalla de Creación de Viaje
        composable(
            route = AppDestinations.CREATE_TRIP_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            CreateTripScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Pantalla de Detalles de Viaje
        composable(
            route = "${AppDestinations.TRIP_DETAILS_ROUTE}/{tripId}",
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            TripDetailsScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(AppDestinations.TRIPS_ROUTE) }
            )
        }
        
        // Pantalla de Configuración
        composable(
            route = AppDestinations.SETTINGS_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { 
                    navController.navigate(AppDestinations.PROFILE_ROUTE) 
                },
                onLogout = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.TRIPS_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Perfil
        composable(
            route = AppDestinations.PROFILE_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Pantalla de Acerca De
        composable(
            route = AppDestinations.ABOUT_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            AboutUsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Pantalla de Términos y Condiciones
        composable(
            route = AppDestinations.TERMS_ROUTE,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            TermsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 