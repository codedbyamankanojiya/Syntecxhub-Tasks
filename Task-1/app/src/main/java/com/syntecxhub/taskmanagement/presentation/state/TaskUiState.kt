package com.syntecxhub.taskmanagement.presentation.state

import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedPriority: Priority? = null,
    val showCompleted: Boolean? = null,
    val error: String? = null
)
