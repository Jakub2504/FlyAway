package com.example.flyaway.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.data.local.dao.AccessLogDao
import com.example.flyaway.data.local.dao.UserDao
import com.example.flyaway.data.local.entity.AccessLogEntity
import com.example.flyaway.data.local.entity.UserEntity
import com.example.flyaway.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDao: UserDao,
    private val accessLogDao: AccessLogDao
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun signIn(usernameOrEmail: String, password: String) {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Por favor, completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Primero intentamos buscar el usuario por nombre de usuario
                val userByUsername = userDao.getUserByUsername(usernameOrEmail)
                val email = if (userByUsername != null) {
                    userByUsername.email
                } else {
                    // Si no encontramos por nombre de usuario, asumimos que es un email
                    usernameOrEmail
                }

                authRepository.signIn(email, password)
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    // Buscar usuario en la base local
                    val localUser = userDao.getUserById(user.uid).first()
                    val userEntity = UserEntity(
                        id = user.uid,
                        email = user.email ?: "",
                        username = localUser?.username ?: user.displayName ?: "",
                        birthdate = localUser?.birthdate ?: "",
                        country = localUser?.country ?: "",
                        phoneNumber = localUser?.phoneNumber ?: user.phoneNumber ?: "",
                        acceptEmails = localUser?.acceptEmails ?: true
                    )
                    userDao.insertUser(userEntity)

                    // Registrar el acceso
                    val accessLog = AccessLogEntity(
                        userId = user.uid,
                        timestamp = LocalDateTime.now(),
                        action = "LOGIN"
                    )
                    accessLogDao.insertAccessLog(accessLog)

                    _state.update { it.copy(isAuthenticated = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        username: String,
        phone: String,
        birthdate: String,
        country: String,
        acceptEmails: Boolean
    ) {
        if (email.isBlank() || password.isBlank() || username.isBlank() || phone.isBlank() || birthdate.isBlank() || country.isBlank()) {
            _state.update { it.copy(error = "Por favor, completa todos los campos") }
            return
        }

        if (password.length < 6) {
            _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(error = "Por favor, introduce un email válido") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Verificar si el nombre de usuario ya existe
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    _state.update { it.copy(error = "Este nombre de usuario ya está en uso") }
                    return@launch
                }

                authRepository.signUp(email, password)
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    // Enviar email de verificación
                    user.sendEmailVerification()

                    // Guardar usuario en la base de datos local
                    val userEntity = UserEntity(
                        id = user.uid,
                        email = email,
                        username = username,
                        birthdate = birthdate,
                        country = country,
                        phoneNumber = phone,
                        acceptEmails = acceptEmails
                    )
                    userDao.insertUser(userEntity)

                    // Registrar el acceso
                    val accessLog = AccessLogEntity(
                        userId = user.uid,
                        timestamp = LocalDateTime.now(),
                        action = "REGISTER"
                    )
                    accessLogDao.insertAccessLog(accessLog)

                    _state.update { it.copy(isAuthenticated = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    // Registrar el acceso
                    val accessLog = AccessLogEntity(
                        userId = user.uid,
                        timestamp = LocalDateTime.now(),
                        action = "LOGOUT"
                    )
                    accessLogDao.insertAccessLog(accessLog)
                }
                authRepository.signOut()
                _state.update { it.copy(isAuthenticated = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _state.update { it.copy(error = "Por favor, introduce tu email") }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(error = "Por favor, introduce un email válido") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.resetPassword(email)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
} 