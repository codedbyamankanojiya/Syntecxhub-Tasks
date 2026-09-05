package com.novachat.app.presentation.chatroom

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.novachat.app.domain.model.Message
import com.novachat.app.domain.model.MessageType
import com.novachat.app.presentation.ui.component.UserProfileBottomSheet
import com.novachat.app.presentation.ui.component.VoiceNoteWaveform
import com.novachat.app.presentation.ui.shape.chatBubbleShape
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatDimens
import com.novachat.app.presentation.ui.theme.NovaChatTypography
import com.novachat.app.presentation.ui.util.NotificationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * ChatRoomScreen — the main real-time chat interface.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val playingMessageId by viewModel.playingMessageId.collectAsStateWithLifecycle()
    val otherUserName by viewModel.otherUserNameState.collectAsStateWithLifecycle()
    val otherUserAvatar by viewModel.otherUserAvatarState.collectAsStateWithLifecycle()
    val otherUser by viewModel.otherUser.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showProfileSheet by remember { mutableStateOf(false) }

    // ── Active Chat Tracker & Notification Clear ────────────────────────────────
    DisposableEffect(viewModel.chatId) {
        NotificationHelper.activeChatId = viewModel.chatId
        NotificationHelper.cancelChatNotification(context, viewModel.chatId)
        onDispose {
            if (NotificationHelper.activeChatId == viewModel.chatId) {
                NotificationHelper.activeChatId = null
            }
        }
    }

    // ── One-shot event handler ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ChatRoomUiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                ChatRoomUiEvent.NavigateBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    // ── Scroll to bottom ONLY when message list grows (not on every state change) ─
    val messages = (uiState as? ChatRoomUiState.Success)?.messages
    val messagesSize = messages?.size ?: 0
    LaunchedEffect(messagesSize) {
        val successState = uiState as? ChatRoomUiState.Success ?: return@LaunchedEffect
        if (successState.scrollToBottom && successState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(successState.messages.lastIndex)
            }
            viewModel.onScrollToBottomConsumed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NovaChatColors.Background)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ─────────────────────────────────────────────────────────
            val successState = uiState as? ChatRoomUiState.Success
            val isOtherUserDeleted = otherUser?.isDeleted == true ||
                                     otherUser?.displayName == "Deleted User" ||
                                     otherUserName == "Deleted User"
            val effectiveName = if (isOtherUserDeleted) "Deleted User" else (otherUser?.displayName ?: otherUserName)
            val effectiveAvatar = if (isOtherUserDeleted) null else (otherUser?.photoUrl ?: otherUserAvatar)

            ChatRoomTopBar(
                name = effectiveName,
                avatarUrl = effectiveAvatar,
                isTyping = if (isOtherUserDeleted) false else (successState?.isTyping ?: false),
                isOnline = if (isOtherUserDeleted) false else (otherUser?.isOnline ?: (successState?.otherUserOnline ?: false)),
                lastSeen = otherUser?.lastSeen ?: (successState?.otherUserLastSeen ?: 0L),
                isDeleted = isOtherUserDeleted,
                onBack = onNavigateBack,
                onProfileClick = { if (!isOtherUserDeleted) showProfileSheet = true }
            )

            // ── Message Feed ─────────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is ChatRoomUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NovaChatColors.Primary)
                        }
                    }
                    is ChatRoomUiState.Success -> {
                        if (state.messages.isEmpty()) {
                            EmptyMessagesPlaceholder(name = effectiveName)
                        } else {
                            MessageFeed(
                                messages = state.messages,
                                otherUserName = effectiveName,
                                listState = listState,
                                playingMessageId = playingMessageId,
                                playbackState = playbackState,
                                onVoicePlayPause = { id, url ->
                                    viewModel.toggleVoicePlayback(id, url)
                                }
                            )
                        }
                    }
                    is ChatRoomUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("⚠️", fontSize = 48.sp)
                                Text(state.message, color = NovaChatColors.TextSecondary)
                            }
                        }
                    }
                }
            }

            // ── Input Bar / Disabled Account Banner ──────────────────────────────
            if (isOtherUserDeleted) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = NovaChatColors.SurfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = NovaChatColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This account has been deleted. Chat is disabled.",
                            style = NovaChatTypography.BodyMedium,
                            color = NovaChatColors.TextSecondary
                        )
                    }
                }
            } else {
                ChatInputBar(
                    inputText = successState?.inputText ?: "",
                    onTextChanged = viewModel::onInputTextChanged,
                    onSend = viewModel::sendText
                )
            }
        }

        // ── Snackbar ──────────────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = NovaChatColors.Surface,
                contentColor = NovaChatColors.TextPrimary
            )
        }
    }

    // ── User Profile Sheet ───────────────────────────────────────────────────────
    if (showProfileSheet) {
        otherUser?.let { user ->
            UserProfileBottomSheet(
                user = user,
                onDismiss = { showProfileSheet = false },
                onStartChat = { showProfileSheet = false }
            )
        }
    }
}

// ─── Empty messages state ──────────────────────────────────────────────────────

@Composable
private fun EmptyMessagesPlaceholder(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("👋", fontSize = 64.sp)
            Text(
                text = "Say hello to $name!",
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.TitleMedium
            )
            Text(
                text = "Start the conversation",
                color = NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodyMedium
            )
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun ChatRoomTopBar(
    name: String,
    avatarUrl: String?,
    isTyping: Boolean,
    isOnline: Boolean,
    lastSeen: Long,
    isDeleted: Boolean = false,
    onBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NovaChatColors.Surface)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = NovaChatColors.TextPrimary
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (!isDeleted) Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onProfileClick)
                    else Modifier
                )
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini avatar with presence dot
            Box(modifier = Modifier.size(44.dp)) {
                if (isDeleted) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.SurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = "Deleted User",
                            tint = NovaChatColors.TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else if (!avatarUrl.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = avatarUrl,
                        contentDescription = "$name avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        error = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NovaChatColors.Primary)
                            ) {
                                Text(
                                    text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = NovaChatColors.TextOnPrimary,
                                    style = NovaChatTypography.TitleMedium
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Primary)
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = NovaChatColors.TextOnPrimary,
                            style = NovaChatTypography.TitleMedium
                        )
                    }
                }
                // Presence dot
                if (isOnline && !isDeleted) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Surface)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Online)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = NovaChatColors.TextPrimary,
                    style = NovaChatTypography.TitleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isDeleted) {
                    Text(
                        text = "Account Deleted",
                        color = NovaChatColors.TextSecondary,
                        style = NovaChatTypography.BodySmall
                    )
                } else {
                    AnimatedContent(
                        targetState = isTyping,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                        label = "typing_status"
                    ) { typing ->
                        if (typing) {
                            TypingIndicator()
                        } else {
                            Text(
                                text = formatLastSeen(isOnline, lastSeen),
                                color = if (isOnline) NovaChatColors.Online else NovaChatColors.TextSecondary,
                                style = NovaChatTypography.BodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = "typing", color = NovaChatColors.Primary, style = NovaChatTypography.BodySmall)
        repeat(3) { idx ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        delayMillis = idx * 160,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$idx"
            )
            Text(
                text = ".",
                color = NovaChatColors.Primary.copy(alpha = alpha),
                style = NovaChatTypography.BodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Message Feed ─────────────────────────────────────────────────────────────

@Composable
private fun MessageFeed(
    messages: List<Message>,
    otherUserName: String,
    listState: androidx.compose.foundation.lazy.LazyListState,
    playingMessageId: String?,
    playbackState: Map<String, Float>,
    onVoicePlayPause: (String, String) -> Unit
) {
    // Group messages by date — computed once per messages list change
    data class DateGroup(val label: String, val messages: List<Message>)

    val grouped = remember(messages) {
        buildList {
            var lastDate = ""
            messages.forEach { msg ->
                val date = formatDate(msg.timestamp)
                if (date != lastDate) {
                    add(DateGroup(date, mutableListOf(msg)))
                    lastDate = date
                } else {
                    (last().messages as MutableList).add(msg)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        grouped.forEach { group ->
            // ── Date Separator ──────────────────────────────────────────────────
            item(key = "date_${group.label}") {
                DateChip(label = group.label)
            }

            // ── Messages ────────────────────────────────────────────────────────
            items(
                items = group.messages,
                key = { it.id }
            ) { message ->
                val groupMessages = group.messages
                val idx = groupMessages.indexOf(message)
                val isFirst = idx == 0 || groupMessages[idx - 1].senderId != message.senderId
                val isLast = idx == groupMessages.lastIndex ||
                        groupMessages[idx + 1].senderId != message.senderId

                val topSpacing = if (isFirst && idx > 0) 6.dp else 1.dp
                Spacer(modifier = Modifier.height(topSpacing))

                MessageBubble(
                    message = message,
                    otherUserName = otherUserName,
                    isFirstInCluster = isFirst,
                    isLastInCluster = isLast,
                    isPlaying = playingMessageId == message.id,
                    playbackProgress = playbackState[message.id] ?: 0f,
                    onVoicePlayPause = onVoicePlayPause
                )
            }
        }
    }
}

@Composable
private fun DateChip(label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NovaChatColors.SurfaceVariant.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.LabelSmall
            )
        }
    }
}

// ─── Message Bubble ───────────────────────────────────────────────────────────

@Composable
private fun MessageBubble(
    message: Message,
    otherUserName: String,
    isFirstInCluster: Boolean,
    isLastInCluster: Boolean,
    isPlaying: Boolean,
    playbackProgress: Float,
    onVoicePlayPause: (String, String) -> Unit
) {
    val isOutgoing = message.isSentByMe
    val bubbleColor = if (isOutgoing) NovaChatColors.PrimaryContainer else NovaChatColors.SurfaceVariant
    val bubbleShape = chatBubbleShape(
        isOutgoing = isOutgoing,
        isFirstInCluster = isFirstInCluster,
        isLastInCluster = isLastInCluster
    )

    val tailPadding = if (isLastInCluster) 10.dp else 4.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isOutgoing) 52.dp else tailPadding,
                end = if (isOutgoing) tailPadding else 52.dp
            ),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 60.dp, max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = if (isFirstInCluster && !isOutgoing) 6.dp else 8.dp,
                    bottom = 6.dp
                )
        ) {
            // Sender name (incoming only, first in cluster)
            if (!isOutgoing && isFirstInCluster) {
                val displayName = otherUserName.takeIf { it.isNotBlank() && it != "User" }
                    ?: message.senderName.takeIf { it.isNotBlank() }
                    ?: "User"
                Text(
                    text = displayName,
                    color = NovaChatColors.Primary,
                    style = NovaChatTypography.LabelSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Content
            when (message.type) {
                MessageType.TEXT -> TextMessageContent(message)
                MessageType.VOICE_NOTE -> VoiceNoteContent(
                    message = message,
                    isPlaying = isPlaying,
                    playbackProgress = playbackProgress,
                    isOutgoing = isOutgoing,
                    onPlayPause = { onVoicePlayPause(message.id, message.content) }
                )
                MessageType.IMAGE -> ImageMessageContent(message)
                else -> TextMessageContent(message) // Fallback
            }
        }
    }
}

@Composable
private fun TextMessageContent(message: Message) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = message.content,
            color = if (message.isSentByMe) NovaChatColors.TextOnPrimary else NovaChatColors.TextPrimary,
            style = NovaChatTypography.BodyMedium,
            modifier = Modifier.weight(1f, fill = false)
        )
        MessageMeta(message = message)
    }
}

@Composable
private fun VoiceNoteContent(
    message: Message,
    isPlaying: Boolean,
    playbackProgress: Float,
    isOutgoing: Boolean,
    onPlayPause: () -> Unit
) {
    VoiceNoteWaveform(
        amplitudes = message.voiceAmplitudes,
        durationMs = if (isPlaying && playbackProgress > 0)
            (message.voiceDurationMs * (1f - playbackProgress)).toLong()
        else message.voiceDurationMs,
        isPlaying = isPlaying,
        playbackProgress = playbackProgress,
        onPlayPauseClick = onPlayPause,
        isOutgoing = isOutgoing,
        modifier = Modifier.width(200.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        MessageMeta(message = message)
    }
}

@Composable
private fun ImageMessageContent(message: Message) {
    SubcomposeAsyncImage(
        model = message.content,
        contentDescription = "Image message",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp, 150.dp)
            .clip(RoundedCornerShape(8.dp))
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        MessageMeta(message = message)
    }
}

@Composable
private fun MessageMeta(message: Message) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val metaColor = if (message.isSentByMe) NovaChatColors.TextOnPrimary.copy(alpha = 0.7f) else NovaChatColors.TextSecondary
        Text(
            text = formatTime(message.timestamp),
            color = metaColor,
            style = NovaChatTypography.LabelSmall
        )
        if (message.isSentByMe) {
            val tickIcon = if (message.isRead || message.isDelivered) Icons.Default.DoneAll else Icons.Default.Check
            val tickColor = if (message.isRead) {
                Color(0xFF34B7F1) // Turns blue when read
            } else {
                Color.White.copy(alpha = 0.85f) // White when not seen
            }
            Icon(
                imageVector = tickIcon,
                contentDescription = when {
                    message.isRead -> "Read"
                    message.isDelivered -> "Delivered"
                    else -> "Sent"
                },
                tint = tickColor,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

// ─── Input Bar ────────────────────────────────────────────────────────────────

@Composable
private fun ChatInputBar(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    val canSend = inputText.isNotBlank()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = NovaChatColors.Surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Text Field
            TextField(
                value = inputText,
                onValueChange = onTextChanged,
                placeholder = {
                    Text("Message", color = NovaChatColors.TextSecondary, style = NovaChatTypography.BodyMedium)
                },
                maxLines = 5,
                textStyle = NovaChatTypography.BodyMedium.copy(color = NovaChatColors.TextPrimary),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = NovaChatColors.InputBackground,
                    unfocusedContainerColor = NovaChatColors.InputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = NovaChatColors.Primary
                ),
                shape = RoundedCornerShape(22.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                modifier = Modifier.weight(1f)
            )

            // Send Button
            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (canSend) NovaChatColors.Primary else NovaChatColors.Primary.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Date / Time Helpers ──────────────────────────────────────────────────────

private fun formatLastSeen(isOnline: Boolean, lastSeen: Long): String {
    if (isOnline) return "online"
    if (lastSeen == 0L) return "offline"

    val now = System.currentTimeMillis()
    val diff = now - lastSeen

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "active just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "active ${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "active ${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val date = Date(lastSeen)
            "active on ${SimpleDateFormat("EEE", Locale.getDefault()).format(date)}"
        }
        else -> {
            val date = Date(lastSeen)
            "active on ${SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)}"
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val cal = Calendar.getInstance()
    val today = Calendar.getInstance()
    cal.timeInMillis = timestamp
    return when {
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 -> "Yesterday"
        else -> SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
