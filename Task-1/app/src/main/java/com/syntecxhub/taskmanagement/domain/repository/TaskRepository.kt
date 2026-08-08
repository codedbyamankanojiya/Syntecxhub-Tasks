package com.syntecxhub.taskmanagement.domain.repository

import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.model.TaskStats
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Int): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    fun getTaskStats(): Flow<TaskStats>
}
