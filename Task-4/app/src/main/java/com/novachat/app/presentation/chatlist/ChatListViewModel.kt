package com.novachat.app.presentation.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novachat.app.domain.model.Chat
import com.novachat.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatListUiState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = true,
    val currentUserId: String = "",
    val errorMessage: String? = null
)

/**
 * ViewModel for [ChatListScreen] — observes the real-time chat list
 * from Firestore via the repository.
 */
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState(
        currentUserId = repository.getCurrentUserId() ?: ""
    ))
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        observeChats()
    }

    private fun observeChats() {
        viewModelScope.launch {
            repository.observeChats()
                .catch { err ->
                    android.util.Log.e("ChatListViewModel", "observeChats error", err)
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.localizedMessage) }
                }
                .collect { chats ->
                    _uiState.update {
                        it.copy(chats = chats, isLoading = false)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun createOrOpenChat(otherUserId: String, onChatReady: (String) -> Unit) {
        viewModelScope.launch {
            repository.getOrCreateChat(otherUserId)
                .onSuccess { chatId -> onChatReady(chatId) }
                .onFailure { err ->
                    _uiState.update { it.copy(errorMessage = err.localizedMessage) }
                }
        }
    }
}
