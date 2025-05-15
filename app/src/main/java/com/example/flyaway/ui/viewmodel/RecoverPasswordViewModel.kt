package com.example.flyaway.ui.viewmodel

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

data class RecoverPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

sealed class RecoverPasswordEvent {
    data class OnEmailChange(val email: String) : RecoverPasswordEvent()
    data object OnRecoverPassword : RecoverPasswordEvent()
}

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecoverPasswordState())
    val state: StateFlow<RecoverPasswordState> = _state.asStateFlow()

    fun onEvent(event: RecoverPasswordEvent) {
        when (event) {
            is RecoverPasswordEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }
            is RecoverPasswordEvent.OnRecoverPassword -> {
                recoverPassword()
            }
        }
    }

    internal fun recoverPassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.resetPassword(state.value.email)
                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
} 