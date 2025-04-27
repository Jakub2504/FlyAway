package com.example.flyaway.ui.navigation

/**
 * Clase sellada que define todas las rutas de navegación de la aplicación.
 * Cada objeto representa una pantalla diferente con su ruta correspondiente.
 */
sealed class AppScreens(val route: String) {
    object SplashScreen : AppScreens("splash_screen")
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object ForgotPasswordScreen : AppScreens("forgot_password_screen")
    object HomeScreen : AppScreens("home_screen")
    object TermsScreen : AppScreens("terms_screen")
    object SettingsScreen : AppScreens("settings_screen")
    object CreateTripScreen : AppScreens("create_trip_screen")
    object TripDetailsScreen : AppScreens("trip_details_screen/{tripId}") {
        fun createRoute(tripId: String) = "trip_details_screen/$tripId"
    }
    object ProfileScreen : AppScreens("profile_screen")
    object AboutScreen : AppScreens("about_screen")
    object LanguageSettingsScreen : AppScreens("language_settings")
    
    // Añadir más pantallas según se necesiten
} 