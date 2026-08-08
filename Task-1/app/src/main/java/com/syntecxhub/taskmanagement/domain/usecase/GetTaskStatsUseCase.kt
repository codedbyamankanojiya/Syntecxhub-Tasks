package com.syntecxhub.taskmanagement.domain.usecase

import com.syntecxhub.taskmanagement.domain.model.TaskStats
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskStatsUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<TaskStats> {
        return repository.getTaskStats()
    }
}
