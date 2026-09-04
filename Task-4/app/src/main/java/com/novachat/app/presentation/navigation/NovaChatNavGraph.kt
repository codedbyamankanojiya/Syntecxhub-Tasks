package com.novachat.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.novachat.app.presentation.auth.AuthScreen
import com.novachat.app.presentation.chatlist.ChatListViewModel
import com.novachat.app.presentation.chatroom.ChatRoomScreen
import com.novachat.app.presentation.main.MainScreen
import com.novachat.app.presentation.search.UserSearchScreen
import com.novachat.app.presentation.splash.SplashScreen

/**
 * NovaChat navigation destinations.
 */
sealed class NavDestination(val route: String) {
    data object Splash : NavDestination("splash")
    data object Auth : NavDestination("auth")
    data object Main : NavDestination("main")
    data object UserSearch : NavDestination("user_search")
    data object ChatRoom : NavDestination("chat_room/{chatId}/{otherUserId}") {
        fun buildRoute(
            chatId: String,
            otherUserId: String
        ): String = "chat_room/$chatId/$otherUserId"
    }
}

/**
 * Root Compose navigation graph for NovaChat.
 *
 * @param isAuthenticated Whether user is currently authenticated when exiting splash.
 * @param startDestination The initial screen (defaults to Splash).
 */
@Composable
fun NovaChatNavGraph(
    isAuthenticated: Boolean = false,
    startDestination: String = NavDestination.Splash.route
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Splash ────────────────────────────────────────────────────────────
        composable(NavDestination.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val nextRoute = if (isAuthenticated) NavDestination.Main.route else NavDestination.Auth.route
                    navController.navigate(nextRoute) {
                        popUpTo(NavDestination.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        // ── Auth ──────────────────────────────────────────────────────────────
        composable(NavDestination.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(NavDestination.Main.route) {
                        popUpTo(NavDestination.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main (Chats + Profile) ────────────────────────────────────────────
        composable(NavDestination.Main.route) {
            val chatListViewModel: ChatListViewModel = hiltViewModel()
            val chatListState by chatListViewModel.uiState.collectAsState()
            val currentUserId = chatListState.currentUserId

            MainScreen(
                onChatClicked = { chat ->
                    val otherUserId = chat.participantIds
                        .firstOrNull { it != currentUserId && it.isNotEmpty() }
                        ?: chat.participantIds.firstOrNull { it.isNotEmpty() }
                        ?: ""
                    navController.navigate(
                        NavDestination.ChatRoom.buildRoute(
                            chatId = chat.id,
                            otherUserId = otherUserId
                        )
                    )
                },
                onNewChat = {
                    navController.navigate(NavDestination.UserSearch.route)
                },
                onSignedOut = {
                    navController.navigate(NavDestination.Auth.route) {
                        popUpTo(NavDestination.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // ── User Search ───────────────────────────────────────────────────────
        composable(NavDestination.UserSearch.route) {
            UserSearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onChatReady = { chatId, otherUserId, _, _ ->
                    navController.navigate(
                        NavDestination.ChatRoom.buildRoute(
                            chatId = chatId,
                            otherUserId = otherUserId
                        )
                    ) {
                        // Pop UserSearch so back from ChatRoom goes to Main
                        popUpTo(NavDestination.Main.route)
                    }
                }
            )
        }

        // ── Chat Room ─────────────────────────────────────────────────────────
        composable(
            route = NavDestination.ChatRoom.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("otherUserId") { type = NavType.StringType }
            )
        ) {
            ChatRoomScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
