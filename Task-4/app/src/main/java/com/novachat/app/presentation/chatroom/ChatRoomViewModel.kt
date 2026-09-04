package com.novachat.app.presentation.chatroom

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novachat.app.domain.repository.ChatRepository
import com.novachat.app.domain.usecase.ObserveMessagesUseCase
import com.novachat.app.domain.usecase.SendTextMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import com.novachat.app.domain.model.User
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

/**
 * ViewModel for [ChatRoomScreen].
 *
 * Responsibilities:
 * - Subscribes to the real-time message stream via [ObserveMessagesUseCase].
 * - Manages input text and debounces typing indicator broadcasts.
 * - Controls MediaRecorder lifecycle for in-app voice note recording.
 * - Controls MediaPlayer lifecycle for voice note playback.
 * - Exposes [ChatRoomUiState] as a [StateFlow] and [ChatRoomUiEvent] as a channel.
 */
@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val observeMessages: ObserveMessagesUseCase,
    private val sendTextMessage: SendTextMessageUseCase,
    private val repository: ChatRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /** The chat room ID passed via navigation arguments. */
    val chatId: String = checkNotNull(savedStateHandle["chatId"])

    /** The remote participant's UID passed via navigation arguments. */
    val otherUserId: String = checkNotNull(savedStateHandle["otherUserId"])

    /** The remote participant's display name for the top bar. */
    private val otherUserNameInitial: String = savedStateHandle.get<String>("otherUserName") ?: "User"
    val otherUserNameState = MutableStateFlow(otherUserNameInitial)

    private val otherUserAvatarInitial: String? = savedStateHandle.get<String>("otherUserAvatar")
    val otherUserAvatarState = MutableStateFlow(otherUserAvatarInitial)

    /** Real-time profile state for the remote participant. */
    val otherUser: StateFlow<User?> = repository.observeUser(otherUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = User(
                uid = otherUserId,
                displayName = otherUserNameInitial,
                photoUrl = otherUserAvatarInitial
            )
        )

    // ─── UI State ─────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<ChatRoomUiState>(ChatRoomUiState.Loading)
    val uiState: StateFlow<ChatRoomUiState> = _uiState.asStateFlow()

    // ─── One-shot Events ──────────────────────────────────────────────────────

    private val _events = Channel<ChatRoomUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // ─── Typing Debounce ──────────────────────────────────────────────────────

    private var typingDebounceJob: Job? = null
    private val TYPING_DEBOUNCE_MS = 2_000L

    // ─── Voice Recording ──────────────────────────────────────────────────────

    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null
    private val capturedAmplitudes = mutableListOf<Float>()
    private var amplitudeSamplerJob: Job? = null
    private var recordingTimerJob: Job? = null

    // ─── Voice Playback ───────────────────────────────────────────────────────

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingMessageId: String? = null

    // ─── Playback state (messageId → progress 0f..1f) ────────────────────────
    private val _playbackState = MutableStateFlow<Map<String, Float>>(emptyMap())
    val playbackState: StateFlow<Map<String, Float>> = _playbackState.asStateFlow()

    private val _playingMessageId = MutableStateFlow<String?>(null)
    val playingMessageId: StateFlow<String?> = _playingMessageId.asStateFlow()

    // ─── Init ─────────────────────────────────────────────────────────────────

    init {
        observeRoomMessages()
        observeTyping()
        observeOtherUserPresence()
        markRead()
    }

    private fun observeRoomMessages() {
        viewModelScope.launch {
            observeMessages(chatId)
                .catch { err ->
                    _uiState.value = ChatRoomUiState.Error(
                        err.localizedMessage ?: "Failed to load messages"
                    )
                }
                .collect { messages ->
                    val currentState = _uiState.value
                    val currentInput = if (currentState is ChatRoomUiState.Success)
                        currentState.inputText else ""
                    val wasRecording = if (currentState is ChatRoomUiState.Success)
                        currentState.isRecording else false
                    val otherOnline = if (currentState is ChatRoomUiState.Success)
                        currentState.otherUserOnline else false
                    val otherLastSeen = if (currentState is ChatRoomUiState.Success)
                        currentState.otherUserLastSeen else 0L

                    _uiState.update {
                        ChatRoomUiState.Success(
                            messages = messages,
                            isTyping = (it as? ChatRoomUiState.Success)?.isTyping ?: false,
                            otherUserOnline = otherOnline,
                            otherUserLastSeen = otherLastSeen,
                            isSendingMessage = false,
                            inputText = currentInput,
                            isRecording = wasRecording,
                            recordingDurationMs = (it as? ChatRoomUiState.Success)?.recordingDurationMs
                                ?: 0L,
                            scrollToBottom = true
                        )
                    }
                }
        }
    }

    private fun observeTyping() {
        viewModelScope.launch {
            repository.observeTypingStatus(chatId, otherUserId)
                .collect { isTyping ->
                    _uiState.update { state ->
                        if (state is ChatRoomUiState.Success)
                            state.copy(isTyping = isTyping)
                        else state
                    }
                }
        }
    }

    private fun observeOtherUserPresence() {
        viewModelScope.launch {
            repository.observeUser(otherUserId).collect { user ->
                user?.let { u ->
                    _uiState.update { state ->
                        if (state is ChatRoomUiState.Success) {
                            state.copy(
                                otherUserOnline = u.isOnline,
                                otherUserLastSeen = u.lastSeen
                            )
                        } else state
                    }
                    // Update potentially changed name/avatar
                    otherUserNameState.value = u.displayName
                    otherUserAvatarState.value = u.photoUrl
                }
            }
        }
    }

    private fun markRead() {
        viewModelScope.launch {
            repository.markMessagesAsRead(chatId)
        }
    }

    // ─── Input Handling ───────────────────────────────────────────────────────

    fun onInputTextChanged(text: String) {
        _uiState.update { state ->
            if (state is ChatRoomUiState.Success) state.copy(inputText = text) else state
        }
        // Broadcast typing started
        viewModelScope.launch {
            repository.sendTypingIndicator(chatId, true)
        }
        // Debounce to broadcast typing stopped
        typingDebounceJob?.cancel()
        typingDebounceJob = viewModelScope.launch {
            delay(TYPING_DEBOUNCE_MS)
            repository.sendTypingIndicator(chatId, false)
        }
    }

    // ─── Send Text ────────────────────────────────────────────────────────────

    fun sendText() {
        val currentState = _uiState.value as? ChatRoomUiState.Success ?: return
        val text = currentState.inputText.trim()
        if (text.isBlank()) return

        // Optimistically clear input
        _uiState.update { state ->
            if (state is ChatRoomUiState.Success)
                state.copy(inputText = "", isSendingMessage = true)
            else state
        }

        viewModelScope.launch {
            sendTextMessage(chatId, text).onFailure { err ->
                _events.send(ChatRoomUiEvent.ShowSnackbar(
                    err.localizedMessage ?: "Failed to send message"
                ))
                // Restore input on failure
                _uiState.update { state ->
                    if (state is ChatRoomUiState.Success)
                        state.copy(inputText = text, isSendingMessage = false)
                    else state
                }
            }
            // Clear typing indicator after sending
            repository.sendTypingIndicator(chatId, false)
        }
    }

    // ─── Send Image ───────────────────────────────────────────────────────────

    fun sendImage(imageFile: File) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state is ChatRoomUiState.Success) state.copy(isSendingMessage = true) else state
            }
            repository.sendImage(chatId, imageFile).onFailure { err ->
                _events.send(ChatRoomUiEvent.ShowSnackbar(
                    err.localizedMessage ?: "Failed to send image"
                ))
            }
            _uiState.update { state ->
                if (state is ChatRoomUiState.Success) state.copy(isSendingMessage = false) else state
            }
        }
    }

    // ─── Voice Note Recording ─────────────────────────────────────────────────

    fun startRecording() {
        capturedAmplitudes.clear()

        val outFile = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        recordingFile = outFile

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128_000)
            setOutputFile(outFile.absolutePath)
            prepare()
            start()
        }

        _uiState.update { state ->
            if (state is ChatRoomUiState.Success) state.copy(isRecording = true, recordingDurationMs = 0L)
            else state
        }

        // Sample amplitude every 100ms for waveform data
        amplitudeSamplerJob = viewModelScope.launch {
            while (isActive) {
                delay(100)
                val maxAmplitude = mediaRecorder?.maxAmplitude ?: 0
                val normalized = (abs(maxAmplitude) / 32768f).coerceIn(0f, 1f)
                capturedAmplitudes.add(normalized)
            }
        }

        // Update recording duration every second
        recordingTimerJob = viewModelScope.launch {
            var elapsed = 0L
            while (isActive) {
                delay(1_000)
                elapsed += 1_000
                _uiState.update { state ->
                    if (state is ChatRoomUiState.Success) state.copy(recordingDurationMs = elapsed)
                    else state
                }
            }
        }
    }

    fun stopAndSendRecording() {
        amplitudeSamplerJob?.cancel()
        recordingTimerJob?.cancel()

        val currentState = _uiState.value as? ChatRoomUiState.Success
        val durationMs = currentState?.recordingDurationMs ?: 0L

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // MediaRecorder may throw if stopped before start
        }
        mediaRecorder = null

        val file = recordingFile ?: return
        val amplitudes = capturedAmplitudes.toList()

        _uiState.update { state ->
            if (state is ChatRoomUiState.Success)
                state.copy(isRecording = false, recordingDurationMs = 0L)
            else state
        }

        viewModelScope.launch {
            repository.sendVoiceNote(
                chatId = chatId,
                audioFile = file,
                amplitudes = amplitudes,
                durationMs = durationMs
            ).onFailure { err ->
                _events.send(ChatRoomUiEvent.ShowSnackbar(
                    err.localizedMessage ?: "Failed to send voice note"
                ))
            }
        }
    }

    fun cancelRecording() {
        amplitudeSamplerJob?.cancel()
        recordingTimerJob?.cancel()
        runCatching {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        }
        mediaRecorder = null
        recordingFile?.delete()
        recordingFile = null
        capturedAmplitudes.clear()

        _uiState.update { state ->
            if (state is ChatRoomUiState.Success)
                state.copy(isRecording = false, recordingDurationMs = 0L)
            else state
        }
    }

    // ─── Voice Playback ───────────────────────────────────────────────────────

    fun toggleVoicePlayback(messageId: String, audioUrl: String) {
        if (currentlyPlayingMessageId == messageId) {
            // Pause currently playing
            mediaPlayer?.pause()
            currentlyPlayingMessageId = null
            _playingMessageId.value = null
        } else {
            // Stop any existing playback
            stopPlayback()
            currentlyPlayingMessageId = messageId
            _playingMessageId.value = messageId

            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    startProgressTracking(messageId, mp)
                }
                setOnCompletionListener {
                    currentlyPlayingMessageId = null
                    _playingMessageId.value = null
                    _playbackState.update { it - messageId }
                    release()
                    mediaPlayer = null
                }
            }
        }
    }

    private fun startProgressTracking(messageId: String, player: MediaPlayer) {
        viewModelScope.launch {
            while (isActive && player.isPlaying) {
                val progress = player.currentPosition.toFloat() / player.duration.toFloat()
                _playbackState.update { it + (messageId to progress.coerceIn(0f, 1f)) }
                delay(80)
            }
        }
    }

    private fun stopPlayback() {
        runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null
        currentlyPlayingMessageId = null
        _playingMessageId.value = null
    }

    // ─── Attachment events ────────────────────────────────────────────────────

    fun onAttachmentClicked() {
        viewModelScope.launch { _events.send(ChatRoomUiEvent.OpenAttachmentSheet) }
    }

    fun onGallerySelected() {
        viewModelScope.launch { _events.send(ChatRoomUiEvent.OpenGalleryPicker) }
    }

    fun onCameraSelected() {
        viewModelScope.launch { _events.send(ChatRoomUiEvent.OpenCamera) }
    }

    // ─── Scroll consumed ─────────────────────────────────────────────────────

    fun onScrollToBottomConsumed() {
        _uiState.update { state ->
            if (state is ChatRoomUiState.Success) state.copy(scrollToBottom = false) else state
        }
    }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        typingDebounceJob?.cancel()
        amplitudeSamplerJob?.cancel()
        recordingTimerJob?.cancel()
        runCatching { mediaRecorder?.stop(); mediaRecorder?.release() }
        stopPlayback()
        viewModelScope.launch { repository.sendTypingIndicator(chatId, false) }
    }
}
