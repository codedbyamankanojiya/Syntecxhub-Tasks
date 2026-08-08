package com.syntecxhub.taskmanagement.domain.usecase

import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository

class ToggleTaskCompletionUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Task {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        repository.updateTask(updatedTask)
        return updatedTask
    }
}
