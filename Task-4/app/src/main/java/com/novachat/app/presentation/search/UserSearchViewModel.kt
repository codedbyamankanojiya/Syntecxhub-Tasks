package com.novachat.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novachat.app.domain.model.User
import com.novachat.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSearchUiState(
    val query: String = "",
    val results: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String? = null
)

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSearchUiState(
        currentUserId = repository.getCurrentUserId()
    ))
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        performSearch("")
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotBlank()) {
                delay(300)
            }
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.searchUsers(query)
                .onSuccess { users ->
                    _uiState.update { state ->
                        state.copy(
                            results = users.filter { u -> u.uid != state.currentUserId },
                            isLoading = false
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { state ->
                        state.copy(
                            errorMessage = err.localizedMessage ?: "Search failed",
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun createChat(otherUser: User, onChatReady: (chatId: String, otherUser: User) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getOrCreateChat(otherUser.uid)
                .onSuccess { chatId ->
                    _uiState.update { it.copy(isLoading = false) }
                    onChatReady(chatId, otherUser)
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.localizedMessage ?: "Failed to create chat"
                        )
                    }
                }
        }
    }

    fun createChat(otherUserId: String, onChatReady: (chatId: String, otherUser: User) -> Unit) {
        val user = _uiState.value.results.find { it.uid == otherUserId }
        if (user != null) {
            createChat(user, onChatReady)
        } else {
            viewModelScope.launch {
                repository.getOrCreateChat(otherUserId)
                    .onSuccess { chatId ->
                        onChatReady(chatId, User(uid = otherUserId, displayName = "User"))
                    }
                    .onFailure { err ->
                        _uiState.update { it.copy(errorMessage = err.localizedMessage ?: "Failed to create chat") }
                    }
            }
        }
    }
}
