package com.syntecxhub.taskmanagement.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.syntecxhub.taskmanagement.MainActivity
import com.syntecxhub.taskmanagement.R
import com.syntecxhub.taskmanagement.data.local.TaskDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val taskDao: TaskDao
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "synctask_reminders"
        const val CHANNEL_NAME = "Task Reminders"
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_TASK_DESCRIPTION = "task_description"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = "Notifications for task due dates and reminders"
                    enableVibration(true)
                    enableLights(true)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt(EXTRA_TASK_ID, -1)
        val taskTitle = inputData.getString(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val taskDescription = inputData.getString(EXTRA_TASK_DESCRIPTION) ?: "You have a task due."

        if (taskId == -1) return Result.failure()

        createNotificationChannel(context)
        showNotification(taskId, taskTitle, taskDescription)

        return Result.success()
    }

    private fun showNotification(taskId: Int, title: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_task_id", taskId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(taskId, notification)
        }
    }
}
