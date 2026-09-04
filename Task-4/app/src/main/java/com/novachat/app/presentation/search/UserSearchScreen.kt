package com.novachat.app.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.novachat.app.domain.model.User
import com.novachat.app.presentation.ui.component.UserProfileBottomSheet
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(
    viewModel: UserSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onChatReady: (chatId: String, otherUserId: String, otherUserName: String, otherUserAvatar: String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedUserForProfile by remember { mutableStateOf<User?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChanged,
                        placeholder = {
                            Text("Search users...", color = NovaChatColors.TextSecondary, style = NovaChatTypography.BodyLarge)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NovaChatColors.InputBackground,
                            unfocusedContainerColor = NovaChatColors.InputBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = NovaChatColors.TextPrimary,
                            unfocusedTextColor = NovaChatColors.TextPrimary,
                            cursorColor = NovaChatColors.Primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NovaChatColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NovaChatColors.Surface
                )
            )
        },
        containerColor = NovaChatColors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = NovaChatColors.Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // No results and query non-blank
                uiState.results.isEmpty() && uiState.query.isNotBlank() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🔍", fontSize = 48.sp)
                        Text(
                            text = "No users found for \"${uiState.query}\"",
                            color = NovaChatColors.TextSecondary,
                            style = NovaChatTypography.BodyLarge
                        )
                    }
                }
                // Idle state: query blank and results empty
                uiState.results.isEmpty() -> {
                    SearchIdleState(modifier = Modifier.align(Alignment.Center))
                }
                // Results or Discover list
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        if (uiState.query.isBlank()) {
                            item {
                                Text(
                                    text = "Discover People",
                                    color = NovaChatColors.Primary,
                                    style = NovaChatTypography.LabelMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                )
                            }
                        }

                        items(uiState.results, key = { it.uid }) { user ->
                            UserSearchItem(
                                user = user,
                                onAvatarClick = { selectedUserForProfile = user },
                                onClick = {
                                    if (!uiState.isLoading) {
                                        viewModel.createChat(user) { chatId, otherUser ->
                                            onChatReady(chatId, otherUser.uid, otherUser.displayName, otherUser.photoUrl)
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 76.dp),
                                color = NovaChatColors.Divider
                            )
                        }
                    }
                }
            }
        }
    }

    selectedUserForProfile?.let { targetUser ->
        UserProfileBottomSheet(
            user = targetUser,
            onDismiss = { selectedUserForProfile = null },
            onStartChat = {
                selectedUserForProfile = null
                viewModel.createChat(targetUser) { chatId, otherUser ->
                    onChatReady(chatId, otherUser.uid, otherUser.displayName, otherUser.photoUrl)
                }
            }
        )
    }
}

@Composable
private fun SearchIdleState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "👤", fontSize = 56.sp)
        Text(
            text = "Find People",
            color = NovaChatColors.TextPrimary,
            style = NovaChatTypography.TitleMedium
        )
        Text(
            text = "No other registered users found",
            color = NovaChatColors.TextSecondary,
            style = NovaChatTypography.BodyMedium
        )
    }
}

@Composable
fun UserSearchItem(
    user: User,
    onAvatarClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NovaChatColors.Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online dot — clickable to open profile sheet
        Box(
            modifier = Modifier
                .size(52.dp)
                .clickable(onClick = onAvatarClick)
        ) {
            if (!user.photoUrl.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${user.displayName} avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    loading = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NovaChatColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = NovaChatColors.TextOnPrimary,
                                style = NovaChatTypography.TitleMedium
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NovaChatColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = NovaChatColors.TextOnPrimary,
                                style = NovaChatTypography.TitleMedium
                            )
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NovaChatColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = NovaChatColors.TextOnPrimary,
                        style = NovaChatTypography.TitleMedium
                    )
                }
            }
            // Online indicator
            if (user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(NovaChatColors.Surface)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(NovaChatColors.Online)
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                color = NovaChatColors.TextPrimary,
                style = NovaChatTypography.TitleMedium
            )
            Text(
                text = if (user.isOnline) "Online" else (user.email ?: ""),
                color = if (user.isOnline) NovaChatColors.Online else NovaChatColors.TextSecondary,
                style = NovaChatTypography.BodySmall
            )
        }
    }
}
