package com.example.flyaway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

sealed class AuthEvent {
    data class Login(val email: String, val password: String) : AuthEvent()
    data class Register(val email: String, val password: String) : AuthEvent()
    data object Logout : AuthEvent()
    data class ResetPassword(val email: String) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        _state.update { it.copy(isAuthenticated = authRepository.currentUser != null) }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            is AuthEvent.Register -> register(event.email, event.password)
            is AuthEvent.Logout -> logout()
            is AuthEvent.ResetPassword -> resetPassword(event.email)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.login(email, password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun register(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.register(email, password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.logout()
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isAuthenticated = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun resetPassword(email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
} 