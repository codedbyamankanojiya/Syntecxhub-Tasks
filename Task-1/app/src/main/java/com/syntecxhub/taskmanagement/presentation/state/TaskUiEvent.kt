package com.syntecxhub.taskmanagement.presentation.state

import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task

sealed interface TaskUiEvent {
    data class SearchQueryChanged(val query: String) : TaskUiEvent
    data class PriorityFilterChanged(val priority: Priority?) : TaskUiEvent
    data class CompletionFilterChanged(val showCompleted: Boolean?) : TaskUiEvent
    data class AddTask(val task: Task) : TaskUiEvent
    data class ToggleTaskCompletion(val task: Task) : TaskUiEvent
    data class DeleteTask(val task: Task) : TaskUiEvent
    data object RefreshTasks : TaskUiEvent
}
