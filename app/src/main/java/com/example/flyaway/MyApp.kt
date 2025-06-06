package com.example.flyaway

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.example.flyaway.data.local.FlyAwayDatabase
import com.example.flyaway.utils.PreferencesRepository
import com.example.flyaway.utils.LocaleManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import java.io.File

@HiltAndroidApp
class MyApp : Application() {
    
    @Inject
    lateinit var preferencesRepository: PreferencesRepository
    
    @Inject
    lateinit var database: FlyAwayDatabase
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase con mejor manejo de errores y logging detallado
        try {
            Log.d("MyApp", "Intentando inicializar Firebase...")
            
            // Verificar si ya está inicializado
            val apps = FirebaseApp.getApps(this)
            Log.d("MyApp", "Firebase apps count: ${apps.size}")
            
            if (apps.isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MyApp", "Firebase inicializado correctamente")
            } else {
                Log.d("MyApp", "Firebase ya estaba inicializado")
            }
            
            // Verificar la configuración
            val firebaseApp = FirebaseApp.getInstance()
            Log.d("MyApp", "Firebase App Name: ${firebaseApp.name}")
            Log.d("MyApp", "Firebase App Options: ${firebaseApp.options}")
            
            // Verificar la autenticación
            val auth = FirebaseAuth.getInstance()
            Log.d("MyApp", "Firebase Auth initialized: ${auth != null}")
            
            // Verificar que la configuración sea válida
            if (auth == null) {
                throw IllegalStateException("Firebase Auth no se pudo inicializar")
            }
            
            // Verificar que el archivo google-services.json esté presente
            val googleServicesFile = File(filesDir, "google-services.json")
            if (!googleServicesFile.exists()) {
                Log.e("MyApp", "google-services.json no encontrado en ${googleServicesFile.absolutePath}")
                throw IllegalStateException("google-services.json no encontrado")
            }
            
        } catch (e: Exception) {
            Log.e("MyApp", "Error al inicializar Firebase", e)
            e.printStackTrace()
            // Forzar una nueva inicialización
            try {
                FirebaseApp.initializeApp(this)
                Log.d("MyApp", "Firebase reinicializado después de error")
            } catch (e2: Exception) {
                Log.e("MyApp", "Error al reinicializar Firebase", e2)
            }
        }
        
        // Inicializar la base de datos
        applicationScope.launch {
            try {
                database.tripDao().getAllTrips().first()
                Log.d("MyApp", "Base de datos inicializada correctamente")
            } catch (e: Exception) {
                Log.e("MyApp", "Error al inicializar la base de datos", e)
            }
        }
        
        // Configurar el idioma al iniciar la aplicación
        applicationScope.launch {
            try {
                // Obtener el idioma guardado o usar el predeterminado (español)
                val savedLanguage = preferencesRepository.getLanguage().first()
                val languageToApply = savedLanguage ?: LocaleManager.DEFAULT_LANGUAGE
                
                // Aplicar el idioma
                val newContext = LocaleManager.setLocale(this@MyApp, languageToApply)
                updateConfigurationWithNewContext(newContext)
                Log.d("MyApp", "Idioma configurado: $languageToApply")
                
                // Si no hay idioma guardado, guardar el predeterminado (español)
                if (savedLanguage == null) {
                    Log.d("MyApp", "Guardando idioma predeterminado: ${LocaleManager.DEFAULT_LANGUAGE}")
                    preferencesRepository.saveLanguage(LocaleManager.DEFAULT_LANGUAGE)
                } else {
                    Log.d("MyApp", "Idioma ya guardado: $savedLanguage")
                }
            } catch (e: Exception) {
                Log.e("MyApp", "Error al configurar el idioma", e)
                
                // En caso de error, intentar aplicar el idioma predeterminado
                val newContext = LocaleManager.setLocale(this@MyApp, LocaleManager.DEFAULT_LANGUAGE)
                updateConfigurationWithNewContext(newContext)
                
                // Guardar el idioma predeterminado si hay error
                try {
                    preferencesRepository.saveLanguage(LocaleManager.DEFAULT_LANGUAGE)
                } catch (e2: Exception) {
                    Log.e("MyApp", "Error al guardar el idioma predeterminado", e2)
                }
            }
        }
    }
    
    override fun attachBaseContext(base: Context) {
        // Intentar obtener el idioma guardado en preferences de manera síncrona
        var languageToApply = LocaleManager.DEFAULT_LANGUAGE
        
        if (::preferencesRepository.isInitialized) {
            try {
                languageToApply = runBlocking { 
                    preferencesRepository.getLanguage().first() 
                } ?: LocaleManager.DEFAULT_LANGUAGE
            } catch (e: Exception) {
                Log.e("MyApp", "Error al obtener el idioma en attachBaseContext", e)
            }
        } else {
            Log.d("MyApp", "preferencesRepository no inicializado, usando idioma predeterminado")
        }
        
        // Aplicar el idioma al contexto base
        val context = LocaleManager.setLocale(base, languageToApply)
        super.attachBaseContext(context)
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Mantener la configuración de idioma cuando cambia la configuración del dispositivo
        applicationScope.launch {
            try {
                val savedLanguage = preferencesRepository.getLanguage().first() ?: LocaleManager.DEFAULT_LANGUAGE
                val newContext = LocaleManager.setLocale(this@MyApp, savedLanguage)
                updateConfigurationWithNewContext(newContext)
                Log.d("MyApp", "Manteniendo idioma en onConfigurationChanged: $savedLanguage")
            } catch (e: Exception) {
                Log.e("MyApp", "Error al mantener el idioma en onConfigurationChanged", e)
            }
        }
    }
    
    private fun updateConfigurationWithNewContext(context: Context) {
        val configuration = context.resources.configuration
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
} 