package com.example.flyaway.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flyaway.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    suspend fun signIn(onNavigateToHome: () -> Unit) {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            authRepository.signIn(_state.value.email, _state.value.password)
            onNavigateToHome()
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message ?: "Error al iniciar sesión") }
        } finally {
            _state.update { it.copy(isLoading = false) }
        }
    }
} 