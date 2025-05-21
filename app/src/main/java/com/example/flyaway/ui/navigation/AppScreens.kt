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
    object HomeHotel : AppScreens("home_hotel")
    object LanguageSettingsScreen : AppScreens("language_settings")
    object NotificationsScreen : AppScreens("notifications_screen")
    object PrivacyPolicyScreen : AppScreens("privacy_policy_screen")
    object ContactUsScreen : AppScreens("contact_us_screen")
    object FeedbackScreen : AppScreens("feedback_screen")
    object AllReservationsScreen : AppScreens("all_reservations_screen")
    object MyReservationsScreen : AppScreens("my_reservations_screen")
    object HotelDetailsScreen : AppScreens("hotel/{hotelId}/{startDate}/{endDate}") {
        fun createRoute(hotelId: String, startDate: String, endDate: String) =
            "hotel/$hotelId/$startDate/$endDate"
    }
    
    // Añadir más pantallas según se necesiten
} 