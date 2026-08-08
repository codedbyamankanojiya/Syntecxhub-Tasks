package com.syntecxhub.taskmanagement.domain.usecase

import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository

class AddTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long {
        if (task.title.isBlank()) {
            throw IllegalArgumentException("Task title cannot be empty.")
        }
        return repository.insertTask(task)
    }
}
