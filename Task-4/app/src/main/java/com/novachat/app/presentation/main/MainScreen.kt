package com.novachat.app.presentation.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.novachat.app.domain.model.Chat
import com.novachat.app.presentation.chatlist.ChatListScreen
import com.novachat.app.presentation.profile.ProfileScreen
import com.novachat.app.presentation.ui.theme.NovaChatColors
import com.novachat.app.presentation.ui.theme.NovaChatTypography

enum class MainTab {
    CHATS, PROFILE
}

/**
 * Main bottom-nav shell for the app.
 *
 * Scaffold handles:
 *  - System navigation bar insets (NavigationBar sits above it)
 *  - Status bar insets are NOT consumed here — child screens handle them via statusBarsPadding()
 */
@Composable
fun MainScreen(
    onChatClicked: (Chat) -> Unit,
    onNewChat: () -> Unit,
    onSignedOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.CHATS) }

    Scaffold(
        // Don't apply top window insets here; child screens manage their own status bar padding
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = NovaChatColors.Surface,
                contentColor = NovaChatColors.Primary
            ) {
                NavigationBarItem(
                    selected = selectedTab == MainTab.CHATS,
                    onClick = { selectedTab = MainTab.CHATS },
                    icon = {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Chats"
                        )
                    },
                    label = {
                        Text(
                            "Chats",
                            style = NovaChatTypography.LabelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NovaChatColors.Primary,
                        selectedTextColor = NovaChatColors.Primary,
                        unselectedIconColor = NovaChatColors.TextSecondary,
                        unselectedTextColor = NovaChatColors.TextSecondary,
                        // More visible indicator
                        indicatorColor = NovaChatColors.Primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = { selectedTab = MainTab.PROFILE },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(
                            "Profile",
                            style = NovaChatTypography.LabelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NovaChatColors.Primary,
                        selectedTextColor = NovaChatColors.Primary,
                        unselectedIconColor = NovaChatColors.TextSecondary,
                        unselectedTextColor = NovaChatColors.TextSecondary,
                        indicatorColor = NovaChatColors.Primary.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_crossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Don't re-consume insets that Scaffold already accounted for
                .consumeWindowInsets(padding)
        ) { tab ->
            when (tab) {
                MainTab.CHATS -> {
                    ChatListScreen(
                        onChatClicked = onChatClicked,
                        onNewChat = onNewChat
                    )
                }
                MainTab.PROFILE -> {
                    ProfileScreen(
                        onSignedOut = onSignedOut
                    )
                }
            }
        }
    }
}
