package com.example.flyaway.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.flyaway.ui.view.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.example.flyaway.ui.transitions.animations.*

/**
 * Navegación principal de la aplicación.
 * Configura todas las rutas y destinos para la navegación entre pantallas.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ) {
        // Pantalla de Splash
        composable(
            route = AppScreens.SplashScreen.route,
            exitTransition = { splashExitTransition() }
        ) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Login
        composable(
            route = AppScreens.LoginScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Home
        composable(
            route = AppScreens.HomeScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            HomeScreen(
                onNavigateToCreateTrip = {
                    navController.navigate(AppScreens.CreateTripScreen.route)
                },
                onNavigateToTripDetails = { tripId ->
                    navController.navigate(AppScreens.TripDetailsScreen.createRoute(tripId))
                },
                onNavigateToSettings = {
                    navController.navigate(AppScreens.SettingsScreen.route)
                },
                onNavigateToAboutUs = {
                    navController.navigate(AppScreens.AboutScreen.route)
                },
                onNavigateToTerms = {
                    navController.navigate(AppScreens.TermsScreen.route)
                }
            )
        }
        
        // Pantalla de Creación de Viaje
        composable(
            route = AppScreens.CreateTripScreen.route,
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
            route = AppScreens.TripDetailsScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            TripDetailsScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = { 
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Configuración
        composable(
            route = AppScreens.SettingsScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
            popEnterTransition = { popEnterTransition() },
            popExitTransition = { popExitTransition() }
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLanguage = { 
                    navController.navigate(AppScreens.LanguageSettingsScreen.route) 
                },
                onNavigateToProfile = { 
                    navController.navigate(AppScreens.ProfileScreen.route) 
                },
                onLogout = {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Configuración de Idioma
        composable(
            route = AppScreens.LanguageSettingsScreen.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() }
        ) {
            LanguageSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Pantalla de Perfil
        composable(
            route = AppScreens.ProfileScreen.route,
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
            route = AppScreens.AboutScreen.route,
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
            route = AppScreens.TermsScreen.route,
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