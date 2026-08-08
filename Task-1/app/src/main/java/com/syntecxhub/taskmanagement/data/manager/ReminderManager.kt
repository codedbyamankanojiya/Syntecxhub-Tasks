package com.syntecxhub.taskmanagement.data.manager

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.syntecxhub.taskmanagement.data.worker.TaskReminderWorker
import com.syntecxhub.taskmanagement.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(task: Task) {
        if (!task.reminderEnabled || task.isCompleted || task.id == null) {
            cancelReminder(task.id)
            return
        }

        val now = LocalDateTime.now()
        if (!task.dueDate.isAfter(now)) {
            return
        }

        val delay = Duration.between(now, task.dueDate).toMillis()

        val inputData = Data.Builder()
            .putInt(TaskReminderWorker.EXTRA_TASK_ID, task.id)
            .putString(TaskReminderWorker.EXTRA_TASK_TITLE, task.title)
            .putString(TaskReminderWorker.EXTRA_TASK_DESCRIPTION, task.description.ifBlank { "Task is due now!" })
            .build()

        val reminderRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("task_reminder_${task.id}")
            .build()

        TaskReminderWorker.createNotificationChannel(context)

        workManager.enqueueUniqueWork(
            "task_reminder_${task.id}",
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun cancelReminder(taskId: Int?) {
        taskId ?: return
        workManager.cancelUniqueWork("task_reminder_$taskId")
    }
}
