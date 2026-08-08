package com.syntecxhub.taskmanagement.presentation.state

import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import java.time.LocalDateTime

sealed interface TaskUiEvent {
    data class SearchQueryChanged(val query: String) : TaskUiEvent
    data class PriorityFilterChanged(val priority: Priority?) : TaskUiEvent
    data class CompletionFilterChanged(val showCompleted: Boolean?) : TaskUiEvent
    data class AddTask(
        val title: String,
        val description: String,
        val priority: Priority,
        val dueDate: LocalDateTime,
        val reminderEnabled: Boolean
    ) : TaskUiEvent
    data class UpdateTask(
        val task: Task,
        val title: String,
        val description: String,
        val priority: Priority,
        val dueDate: LocalDateTime,
        val reminderEnabled: Boolean
    ) : TaskUiEvent
    data class ToggleTaskCompletion(val task: Task) : TaskUiEvent
    data class DeleteTask(val task: Task) : TaskUiEvent
    data class EditTask(val task: Task?) : TaskUiEvent
    data class ShowAddTaskSheet(val show: Boolean) : TaskUiEvent
    data class ShowStatsSheet(val show: Boolean) : TaskUiEvent
    data class CelebrationCompleted(val taskId: Int) : TaskUiEvent
    data class NotificationPermissionResult(val granted: Boolean) : TaskUiEvent
    data object RefreshTasks : TaskUiEvent
}
