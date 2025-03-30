package com.example.flyaway.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para crear un DataStore de preferencias en el contexto de aplicación
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Repositorio para gestionar las preferencias de la aplicación.
 * Utiliza DataStore para almacenar las preferencias de manera asíncrona y segura.
 */
@Singleton
class PreferencesRepository @Inject constructor(
    private val context: Context
) {
    // Claves para las preferencias
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val TERMS_ACCEPTED_KEY = stringPreferencesKey("terms_accepted")
    }
    
    /**
     * Obtiene el idioma guardado.
     * @return Flow con el código del idioma o null si no se ha guardado.
     */
    fun getLanguage(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY]
        }
    }
    
    /**
     * Guarda el idioma seleccionado.
     * @param language Código del idioma (ej: "es", "en").
     */
    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
    
    /**
     * Obtiene el tema guardado.
     * @return Flow con el tema o null si no se ha guardado.
     */
    fun getTheme(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY]
        }
    }
    
    /**
     * Guarda el tema seleccionado.
     * @param theme Tema (ej: "light", "dark", "system").
     */
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    /**
     * Comprueba si los términos y condiciones han sido aceptados.
     * @return Flow con un booleano indicando si los términos han sido aceptados.
     */
    fun isTermsAccepted(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[TERMS_ACCEPTED_KEY] == "true"
        }
    }
    
    /**
     * Guarda la aceptación de los términos y condiciones.
     * @param accepted True si los términos han sido aceptados.
     */
    suspend fun saveTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TERMS_ACCEPTED_KEY] = if (accepted) "true" else "false"
        }
    }
} 