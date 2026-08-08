package com.syntecxhub.taskmanagement.domain.usecase

import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository

class UpdateTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.updateTask(task)
    }
}
