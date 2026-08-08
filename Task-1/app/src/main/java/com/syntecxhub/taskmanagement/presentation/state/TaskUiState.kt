package com.syntecxhub.taskmanagement.presentation.state

import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.model.TaskStats
import java.time.LocalDateTime

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val stats: TaskStats = TaskStats(0, 0, 0, 0, 0, 0, 0f),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedPriority: Priority? = null,
    val showCompleted: Boolean? = null,
    val error: String? = null,
    val showAddTaskSheet: Boolean = false,
    val editingTask: Task? = null,
    val showStatsSheet: Boolean = false,
    val celebratedTaskId: Int? = null,
    val notificationPermissionGranted: Boolean = false
)
