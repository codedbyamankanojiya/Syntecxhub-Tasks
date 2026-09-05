package com.novachat.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novachat.app.domain.model.User
import com.novachat.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val isSignedOut: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.observeCurrentUser()
                .catch { err ->
                    android.util.Log.w("ProfileViewModel", "observeCurrentUser error: ${err.message}")
                    _uiState.update { it.copy(user = null, isLoading = false) }
                }
                .collect { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
        }
    }

    fun updateProfile(displayName: String, bio: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.updateProfile(displayName, bio)
                .onSuccess {
                    // Optimistic update to UI state to immediately reflect changes
                    _uiState.update { state ->
                        state.copy(
                            isUpdating = false,
                            successMessage = "Profile updated successfully",
                            user = state.user?.copy(displayName = displayName, bio = bio)
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage) }
                }
        }
    }

    fun updateProfilePicture(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.updateProfilePicture(file)
                .onSuccess { url ->
                    _uiState.update { state ->
                        state.copy(
                            isUpdating = false,
                            successMessage = "Profile picture updated",
                            user = state.user?.copy(photoUrl = url)
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage) }
                }
        }
    }

    fun removeProfilePicture() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.removeProfilePicture()
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isUpdating = false,
                            successMessage = "Profile picture removed",
                            user = state.user?.copy(photoUrl = null)
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage) }
                }
        }
    }

    fun updateProfilePictureUrl(url: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.updateProfilePictureUrl(url)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isUpdating = false,
                            successMessage = "Avatar updated",
                            user = state.user?.copy(photoUrl = url)
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage) }
                }
        }
    }

    fun toggleSetting(key: String, newValue: Boolean) {
        viewModelScope.launch {
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    user = state.user?.let { u ->
                        when(key) {
                            "readReceipts" -> u.copy(readReceipts = newValue)
                            "notificationsEnabled" -> u.copy(notificationsEnabled = newValue)
                            "lastSeenVisible" -> u.copy(lastSeenVisible = newValue)
                            "aboutVisible" -> u.copy(aboutVisible = newValue)
                            else -> u
                        }
                    }
                )
            }
            repository.updateSetting(key, newValue)
                .onFailure {
                    // Rollback on failure
                    _uiState.update { state ->
                        state.copy(
                            errorMessage = "Failed to update setting",
                            user = state.user?.let { u ->
                                when(key) {
                                    "readReceipts" -> u.copy(readReceipts = !newValue)
                                    "notificationsEnabled" -> u.copy(notificationsEnabled = !newValue)
                                    "lastSeenVisible" -> u.copy(lastSeenVisible = !newValue)
                                    "aboutVisible" -> u.copy(aboutVisible = !newValue)
                                    else -> u
                                }
                            }
                        )
                    }
                }
        }
    }


    fun changePassword(currentPassword: String, newPassword: String, onComplete: () -> Unit = {}) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All password fields are required") }
            return
        }
        if (newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "New password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isUpdating = false, successMessage = "Password changed successfully") }
                    onComplete()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage ?: "Failed to change password") }
                }
        }
    }

    fun changeEmail(newEmail: String, currentPassword: String, onComplete: () -> Unit = {}) {
        val targetEmail = newEmail.trim()
        if (targetEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and current password are required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            repository.changeEmail(targetEmail, currentPassword)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isUpdating = false,
                            successMessage = "Email updated successfully",
                            user = state.user?.copy(email = targetEmail)
                        )
                    }
                    onComplete()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isUpdating = false, errorMessage = err.localizedMessage ?: "Failed to change email") }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { repository.signOut() }
            _uiState.update { it.copy(isLoading = false, user = null, isSignedOut = true) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            val result = repository.deleteAccount()
            result.onSuccess {
                _uiState.update { it.copy(isUpdating = false, user = null, isSignedOut = true) }
            }.onFailure { err ->
                runCatching { repository.signOut() }
                _uiState.update { 
                    it.copy(
                        isUpdating = false, 
                        user = null, 
                        isSignedOut = true, 
                        errorMessage = err.localizedMessage ?: "Failed to delete account"
                    ) 
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
