package com.example.flyaway.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.utils.PreferencesRepository
import com.example.flyaway.utils.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _state = MutableStateFlow(LanguageSettingsState())
    val state: StateFlow<LanguageSettingsState> = _state.asStateFlow()
    
    init {
        // Cargar el idioma actual
        viewModelScope.launch {
            try {
                val savedLanguage = preferencesRepository.getLanguage().first()
                val currentLanguage = savedLanguage ?: LocaleManager.getCurrentLanguageTag(context)
                Log.d("LanguageViewModel", "Idioma actual cargado: $currentLanguage")
                _state.update { it.copy(selectedLanguage = currentLanguage) }
            } catch (e: Exception) {
                Log.e("LanguageViewModel", "Error al cargar el idioma", e)
                _state.update { it.copy(error = "Error al cargar la configuración de idioma") }
            }
        }
    }
    
    fun onLanguageSelected(language: String) {
        Log.d("LanguageViewModel", "Idioma seleccionado: $language")
        _state.update { it.copy(selectedLanguage = language) }
    }
    
    fun saveLanguage() {
        viewModelScope.launch {
            try {
                val selectedLanguage = state.value.selectedLanguage
                Log.d("LanguageViewModel", "Guardando idioma: $selectedLanguage")
                
                // Guardar el idioma seleccionado en las preferencias
                preferencesRepository.saveLanguage(selectedLanguage)
                
                // Aplicar el cambio de idioma a nivel de sistema
                val newContext = LocaleManager.setLocale(context, selectedLanguage)
                context.resources.updateConfiguration(newContext.resources.configuration, context.resources.displayMetrics)
                
                // Notificar que el idioma ha cambiado
                _state.update { it.copy(languageChanged = true, error = null) }
                
                // Verificar que el idioma se haya aplicado correctamente
                val currentLanguage = LocaleManager.getCurrentLanguageTag(context)
                Log.d("LanguageViewModel", "Idioma actual después del cambio: $currentLanguage")
                
                // No mostramos error si el idioma es español, ya que puede tener un comportamiento diferente
                if (currentLanguage != selectedLanguage && selectedLanguage != "es") {
                    Log.e("LanguageViewModel", "Error: El idioma no se aplicó correctamente")
                    _state.update { it.copy(error = "Error al aplicar el idioma") }
                }
            } catch (e: Exception) {
                Log.e("LanguageViewModel", "Error al guardar el idioma", e)
                _state.update { it.copy(error = "Error al guardar la configuración de idioma") }
            }
        }
    }
    
    // Reiniciar el estado después de mostrar el diálogo de confirmación
    fun resetLanguageChanged() {
        _state.update { it.copy(languageChanged = false) }
    }
    
    // Limpiar mensaje de error
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class LanguageSettingsState(
    val selectedLanguage: String = "en",
    val isLoading: Boolean = false,
    val error: String? = null,
    val languageChanged: Boolean = false
) 