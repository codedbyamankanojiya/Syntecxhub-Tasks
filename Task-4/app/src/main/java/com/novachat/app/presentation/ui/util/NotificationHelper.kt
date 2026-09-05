package com.novachat.app.presentation.ui.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.novachat.app.MainActivity
import com.novachat.app.R

object NotificationHelper {

    const val CHANNEL_ID = "novachat_messages_channel"
    private const val CHANNEL_NAME = "Chat Messages"
    private const val CHANNEL_DESC = "Notifications for incoming messages in NovaChat"

    /** ID of the chat currently open by the user, if any. */
    @Volatile
    var activeChatId: String? = null

    /**
     * Creates the high-importance notification channel on Android O+.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    /**
     * Posts a notification for an incoming chat message.
     */
    fun showMessageNotification(
        context: Context,
        chatId: String,
        otherUserId: String,
        senderName: String,
        messageText: String
    ) {
        // If the user is currently viewing this exact chat, do not show a notification
        if (activeChatId == chatId) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("chatId", chatId)
            putExtra("otherUserId", otherUserId)
            putExtra("otherUserName", senderName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(chatId.hashCode(), notification)
        } catch (e: SecurityException) {
            android.util.Log.w("NotificationHelper", "Cannot post notification: permission denied", e)
        }
    }

    /**
     * Cancels any active notification for [chatId].
     */
    fun cancelChatNotification(context: Context, chatId: String) {
        try {
            NotificationManagerCompat.from(context).cancel(chatId.hashCode())
        } catch (_: Exception) {}
    }
}
