package com.example.flyaway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.example.flyaway.data.local.dao.UserDao
import kotlinx.coroutines.flow.first

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                val user = userDao.getUserById(userId).first()
                if (user != null) {
                    _state.update {
                        it.copy(
                            name = user.username,
                            email = user.email,
                            phone = user.phoneNumber,
                            birthdate = user.birthdate,
                            country = user.country,
                            acceptEmails = user.acceptEmails
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            phone = firebaseUser.phoneNumber ?: "",
                            birthdate = "",
                            country = "",
                            acceptEmails = false
                        )
                    }
                }
            } else {
                // Si no hay usuario autenticado, limpiar el estado
                _state.update {
                    it.copy(
                        name = "",
                        email = "",
                        phone = "",
                        birthdate = "",
                        country = "",
                        acceptEmails = false
                    )
                }
            }
        }
    }
    
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnNameChange -> {
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email) }
            }
            is ProfileEvent.OnPhoneChange -> {
                _state.update { it.copy(phone = event.phone) }
            }
            is ProfileEvent.OnChangePhoto -> {
                // En una implementación real, mostraríamos el selector de imágenes
            }
            is ProfileEvent.OnEditProfile -> {
                _state.update { it.copy(isEditing = true) }
            }
            is ProfileEvent.OnSaveProfile -> {
                _state.update { it.copy(isEditing = false) }
                saveProfile()
            }
            is ProfileEvent.OnConfirmSave -> {
                // No es necesario hacer nada aquí, simplemente cerrar el diálogo
            }
        }
    }
    
    private fun saveProfile() {
        viewModelScope.launch {
            // En una implementación real, guardaríamos los datos actualizados
            // profileRepository.saveUserProfile(state.value.toUserData())
        }
    }
}

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val birthdate: String = "",
    val country: String = "",
    val acceptEmails: Boolean = false,
    val photoUri: String? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ProfileEvent {
    data class OnNameChange(val name: String) : ProfileEvent()
    data class OnEmailChange(val email: String) : ProfileEvent()
    data class OnPhoneChange(val phone: String) : ProfileEvent()
    object OnChangePhoto : ProfileEvent()
    object OnEditProfile : ProfileEvent()
    object OnSaveProfile : ProfileEvent()
    object OnConfirmSave : ProfileEvent()
} 