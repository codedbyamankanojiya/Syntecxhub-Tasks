package com.syntecxhub.taskmanagement.domain.usecase

import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(
        query: String = "",
        priority: Priority? = null,
        showCompleted: Boolean? = null
    ): Flow<List<Task>> {
        return repository.getTasks().map { tasks ->
            tasks.filter { task ->
                val matchesQuery = task.title.contains(query, ignoreCase = true) ||
                                 task.description.contains(query, ignoreCase = true)
                val matchesPriority = priority == null || task.priority == priority
                val matchesCompletion = showCompleted == null || task.isCompleted == showCompleted

                matchesQuery && matchesPriority && matchesCompletion
            }.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenByDescending { it.priority.ordinal }
                    .thenByDescending { it.createdAt }
            )
        }
    }
}
