package com.novachat.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.novachat.app.domain.repository.ChatRepository
import com.novachat.app.presentation.navigation.NavDestination
import com.novachat.app.presentation.navigation.NovaChatNavGraph
import com.novachat.app.presentation.ui.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single Activity host for the NovaChat Compose UI.
 *
 * - Edge-to-edge rendering enabled via [enableEdgeToEdge].
 * - Authentication state checked at launch to determine start destination.
 * - The Compose navigation graph handles all screen transitions.
 * - Manages notification channel, runtime permissions, and incoming chat message alerts.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: ChatRepository

    private var pendingChatRoute by mutableStateOf<String?>(null)
    private var notificationObserverJob: Job? = null
    private val notifiedTimestamps = mutableMapOf<String, Long>()
    private var isFirstSnapshot = true

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* Permission handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable true edge-to-edge on Android 15+
        // This must be called before super.onCreate to ensure window is correctly set up
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()
        handleNotificationIntent(intent)
        startObservingMessagesForNotifications()

        setContent {
            MaterialTheme {
                NovaChatNavGraph(
                    isAuthenticated = repository.isAuthenticated(),
                    pendingChatRoute = pendingChatRoute,
                    onChatRouteHandled = { pendingChatRoute = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val chatId = intent?.getStringExtra("chatId")
        val otherUserId = intent?.getStringExtra("otherUserId")
        val otherUserName = intent?.getStringExtra("otherUserName") ?: "User"
        if (!chatId.isNullOrEmpty() && !otherUserId.isNullOrEmpty()) {
            pendingChatRoute = NavDestination.ChatRoom.buildRoute(chatId, otherUserId, otherUserName)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startObservingMessagesForNotifications() {
        notificationObserverJob?.cancel()
        notificationObserverJob = lifecycleScope.launch {
            while (isActive) {
                if (repository.isAuthenticated()) {
                    val currentUid = repository.getCurrentUserId()
                    try {
                        repository.observeChats().collect { chats ->
                            if (isFirstSnapshot) {
                                // Populate initial timestamps so existing chats don't fire notifications on startup
                                chats.forEach { chat ->
                                    notifiedTimestamps[chat.id] = chat.lastMessageTime
                                }
                                isFirstSnapshot = false
                            } else {
                                chats.forEach { chat ->
                                    val previousTime = notifiedTimestamps[chat.id] ?: 0L
                                    val isNew = chat.lastMessageTime > previousTime
                                    val isIncoming = chat.lastMessageSenderId != currentUid && chat.lastMessageSenderId.isNotEmpty()

                                    if (isNew) {
                                        notifiedTimestamps[chat.id] = chat.lastMessageTime
                                        if (isIncoming && chat.unreadCount > 0 && chat.lastMessage.isNotBlank()) {
                                            repository.markMessagesAsDelivered(chat.id)
                                            val otherUserId = chat.participantIds.firstOrNull { it != currentUid } ?: ""
                                            NotificationHelper.showMessageNotification(
                                                context = applicationContext,
                                                chatId = chat.id,
                                                otherUserId = otherUserId,
                                                senderName = chat.otherUserName,
                                                messageText = chat.lastMessage
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error observing chats for notifications", e)
                    }
                } else {
                    isFirstSnapshot = true
                    notifiedTimestamps.clear()
                }
                delay(3000)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (repository.isAuthenticated()) {
            lifecycleScope.launch {
                runCatching { repository.updateUserPresence(true) }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (repository.isAuthenticated()) {
            lifecycleScope.launch {
                runCatching { repository.updateUserPresence(false) }
            }
        }
    }
}
