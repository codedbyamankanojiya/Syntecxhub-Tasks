package com.novachat.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novachat.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for [AuthScreen].
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isSignUpMode: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for [AuthScreen] — manages form state and delegates
 * auth operations to [ChatRepository].
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(
        isAuthenticated = repository.isAuthenticated()
    ))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onDisplayNameChanged(name: String) {
        _uiState.update { it.copy(displayName = name) }
    }

    fun toggleSignUpMode() {
        _uiState.update { it.copy(isSignUpMode = !it.isSignUpMode, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password are required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signInWithEmail(state.email.trim(), state.password)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { err ->
                    _uiState.update { s ->
                        s.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Sign in failed")
                    }
                }
        }
    }

    fun signUp() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        val name = state.displayName.trim().ifBlank {
            state.email.substringBefore("@")
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signUpWithEmail(state.email.trim(), state.password, name)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { err ->
                    _uiState.update { s ->
                        s.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Sign up failed")
                    }
                }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signInAnonymously()
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { err ->
                    _uiState.update { s ->
                        s.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Guest sign-in failed")
                    }
                }
        }
    }
}
