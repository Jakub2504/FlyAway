package com.example.flyaway.ui.navigation

/**
 * Clase de utilidad para gestionar las rutas de navegación de la aplicación.
 * Esta clase proporciona constantes y funciones de ayuda para crear rutas con parámetros.
 */
object AppDestinations {
    
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val RESET_PASSWORD_ROUTE = "reset_password"
    const val TRIPS_ROUTE = "trips"
    const val TRIP_DETAILS_ROUTE = "trip_details"
    const val CREATE_TRIP_ROUTE = "create_trip"
    const val SETTINGS_ROUTE = "settings"
    const val PROFILE_ROUTE = "profile"
    const val ABOUT_ROUTE = "about"
    const val TERMS_ROUTE = "terms"
    
    // Rutas con parámetros
    object TripDetails {
        const val TRIP_ID_PARAM = "tripId"
        const val ROUTE = "trip_details/{$TRIP_ID_PARAM}"
        
        fun createRoute(tripId: String): String {
            return "trip_details/$tripId"
        }
    }
    
    // Función auxiliar para construir rutas con parámetros
    fun withParam(route: String, paramName: String, paramValue: String): String {
        return route.replace("{$paramName}", paramValue)
    }
} 