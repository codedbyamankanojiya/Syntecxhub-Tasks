package com.novachat.app.presentation.chatlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import com.novachat.app.domain.model.Chat
import com.novachat.app.domain.model.User
import com.novachat.app.presentation.ui.component.UserProfileBottomSheet
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onChatClicked: (Chat) -> Unit,
    onNewChat: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedChatForProfile by remember { mutableStateOf<Chat?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NovaChatColors.Background)
            .statusBarsPadding()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            ChatListTopBar()

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NovaChatColors.Primary)
                    }
                }
                uiState.chats.isEmpty() -> {
                    EmptyChatListPlaceholder()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = uiState.chats,
                            key = { _, chat -> chat.id }
                        ) { index, chat ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300 + index * 40)) +
                                        slideInVertically(
                                            tween(300 + index * 40)
                                        ) { it / 2 }
                            ) {
                                ChatListItem(
                                    chat = chat,
                                    currentUserId = uiState.currentUserId,
                                    onAvatarClick = { selectedChatForProfile = chat },
                                    onClick = { onChatClicked(chat) }
                                )
                            }
                            if (index < uiState.chats.lastIndex) {
                                HorizontalDivider(
                                    color = NovaChatColors.Divider,
                                    modifier = Modifier.padding(start = 76.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB — properly padded above system navigation bar
        FloatingActionButton(
            onClick = onNewChat,
            containerColor = NovaChatColors.Primary,
            contentColor = NovaChatColors.TextOnPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "New Chat",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    selectedChatForProfile?.let { chat ->
        val otherUserId = chat.participantIds.firstOrNull { it != uiState.currentUserId }
            ?: chat.participantIds.firstOrNull() ?: ""
        UserProfileBottomSheet(
            user = User(
                uid = otherUserId,
                displayName = chat.otherUserName,
                photoUrl = chat.otherUserAvatar,
                isOnline = chat.otherUserOnline
            ),
            onDismiss = { selectedChatForProfile = null },
            onStartChat = {
                selectedChatForProfile = null
                onChatClicked(chat)
            }
        )
    }
}

@Composable
private fun ChatListTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NovaChatColors.Surface)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "NovaChat",
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.HeadlineMedium
            )
            Text(
                text = "Messages",
                color = NovaChatColors.TextSecondary,
                style = NovaChatTypography.LabelMedium
            )
        }
    }
}

@Composable
private fun ChatListItem(
    chat: Chat,
    currentUserId: String,
    onAvatarClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(NovaChatColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarWithPresence(
            avatarUrl = chat.otherUserAvatar,
            displayName = chat.otherUserName,
            isOnline = chat.otherUserOnline,
            onAvatarClick = onAvatarClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.otherUserName,
                    color = NovaChatColors.TextPrimary,
                    style = NovaChatTypography.TitleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = formatTimestamp(chat.lastMessageTime),
                    color = if (chat.unreadCount > 0) NovaChatColors.Primary else NovaChatColors.TextSecondary,
                    style = NovaChatTypography.LabelSmall
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isFromMe = chat.lastMessageSenderId == currentUserId
                val preview = when {
                    chat.lastMessage.isEmpty() -> "Tap to start chatting"
                    isFromMe -> "You: ${chat.lastMessage}"
                    else -> chat.lastMessage
                }
                Text(
                    text = preview,
                    color = if (chat.unreadCount > 0) NovaChatColors.TextPrimary else NovaChatColors.TextSecondary,
                    style = if (chat.unreadCount > 0) NovaChatTypography.BodySmall.copy(fontWeight = FontWeight.SemiBold)
                            else NovaChatTypography.BodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    UnreadBadge(count = chat.unreadCount)
                }
            }
        }
    }
}

@Composable
private fun AvatarWithPresence(
    avatarUrl: String?,
    displayName: String,
    isOnline: Boolean,
    onAvatarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onAvatarClick)
    ) {
        // Avatar
        if (!avatarUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar of $displayName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Primary)
                    ) {
                        Text(
                            text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = NovaChatColors.TextOnPrimary,
                            style = NovaChatTypography.TitleMedium
                        )
                    }
                },
                error = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NovaChatColors.Primary)
                    ) {
                        Text(
                            text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NovaChatColors.Primary)
            ) {
                Text(
                    text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = NovaChatColors.TextOnPrimary,
                    style = NovaChatTypography.TitleMedium
                )
            }
        }

        // Online presence dot — white border ring + green fill
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(NovaChatColors.Surface)   // white border
                    .padding(3.dp)                         // ring width
                    .clip(CircleShape)
                    .background(NovaChatColors.Online)     // green fill
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
            )
        }
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(NovaChatColors.Unread)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = NovaChatColors.TextOnPrimary,
            style = NovaChatTypography.LabelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyChatListPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "💬", fontSize = 72.sp)
            Text(
                text = "No conversations yet",
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.TitleMedium
            )
            Text(
                text = "Tap the  ✏  button to start chatting",
                color = NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodyMedium
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "now"
        diff < TimeUnit.HOURS.toMillis(24) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
        }
        else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
    }
}
