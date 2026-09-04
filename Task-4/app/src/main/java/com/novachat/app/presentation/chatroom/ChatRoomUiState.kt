package com.novachat.app.presentation.chatroom

import com.novachat.app.domain.model.Message

/**
 * Sealed hierarchy representing every possible UI state of the ChatRoomScreen.
 * The ViewModel exposes a single [StateFlow] of this type, and the Compose
 * tree renders a distinct branch for each variant (Unidirectional Data Flow).
 */
sealed class ChatRoomUiState {

    /** Initial loading phase — skeleton shimmer is shown. */
    data object Loading : ChatRoomUiState()

    /**
     * Messages loaded and stream is active.
     *
     * @property messages         Ordered list of messages (oldest → newest).
     * @property isTyping         True when the remote participant is typing.
     * @property isSendingMessage True while a fire-and-forget message write is in flight.
     * @property inputText        Current draft text in the input bar.
     * @property isRecording      True while MediaRecorder is actively capturing audio.
     * @property recordingDurationMs Elapsed recording time in milliseconds.
     * @property scrollToBottom   One-shot flag — consumed by LazyListState.
     */
    data class Success(
        val messages: List<Message> = emptyList(),
        val isTyping: Boolean = false,
        val otherUserOnline: Boolean = false,
        val otherUserLastSeen: Long = 0L,
        val isSendingMessage: Boolean = false,
        val inputText: String = "",
        val isRecording: Boolean = false,
        val recordingDurationMs: Long = 0L,
        val scrollToBottom: Boolean = false
    ) : ChatRoomUiState()

    /**
     * Transient error state with a human-readable message.
     * The ViewModel reverts to [Success] once the user acknowledges the error.
     */
    data class Error(val message: String) : ChatRoomUiState()
}

/**
 * One-shot events emitted by the ViewModel for side effects that cannot
 * be expressed purely through [ChatRoomUiState] (e.g., snackbars, navigation).
 */
sealed class ChatRoomUiEvent {
    /** Show a transient error snackbar. */
    data class ShowSnackbar(val message: String) : ChatRoomUiEvent()

    /** Navigate back to the ChatListScreen. */
    data object NavigateBack : ChatRoomUiEvent()

    /** Signal the composable to launch the system media picker. */
    data object OpenGalleryPicker : ChatRoomUiEvent()

    /** Signal the composable to launch the system camera. */
    data object OpenCamera : ChatRoomUiEvent()

    /** Trigger the attachment bottom sheet. */
    data object OpenAttachmentSheet : ChatRoomUiEvent()
}
