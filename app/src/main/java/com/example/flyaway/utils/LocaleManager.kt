package com.example.flyaway.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.util.Locale

/**
 * Utilidad para gestionar los idiomas en la aplicación.
 * Permite cambiar y persistir el idioma seleccionado por el usuario.
 */
object LocaleManager {
    
    // Idioma predeterminado de la aplicación
    const val DEFAULT_LANGUAGE = "es"
    
    // Idiomas soportados por la aplicación
    val SUPPORTED_LOCALES = mapOf(
        "es" to Locale("es"),
        "en" to Locale("en"),
        "ca" to Locale("ca"),
        "pl" to Locale("pl")
    )
    
    /**
     * Obtiene el idioma actual del sistema.
     * @param context Contexto de la aplicación.
     * @return Código del idioma actual (ej: "es", "en").
     */
    fun getCurrentLanguageTag(context: Context): String {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        
        return currentLocale.language
    }
    
    /**
     * Establece el idioma para un contexto dado.
     * @param context Contexto base.
     * @param language Código de idioma (ej: "es", "en").
     * @return Contexto con el idioma aplicado.
     */
    fun setLocale(context: Context, language: String): Context {
        Log.d("LocaleManager", "Estableciendo idioma: $language")
        val locale = SUPPORTED_LOCALES[language] ?: SUPPORTED_LOCALES[DEFAULT_LANGUAGE]!!
        return setLocaleForContext(context, locale)
    }
    
    /**
     * Aplica un locale específico a un contexto.
     * @param context Contexto base.
     * @param locale Locale a aplicar.
     * @return Contexto con el locale aplicado.
     */
    fun setLocaleForContext(context: Context, locale: Locale): Context {
        Log.d("LocaleManager", "Aplicando locale: ${locale.language}")
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            return context
        }
    }
} 