package com.syntecxhub.taskmanagement.data.repository

import com.syntecxhub.taskmanagement.data.local.TaskDao
import com.syntecxhub.taskmanagement.data.mapper.toDomain
import com.syntecxhub.taskmanagement.data.mapper.toEntity
import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.model.TaskStats
import com.syntecxhub.taskmanagement.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return dao.getTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(id: Int): Task? {
        return dao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long {
        return dao.insertTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(task.toEntity())
    }

    override fun getTaskStats(): Flow<TaskStats> {
        return dao.getTasks().map { tasks ->
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val highPriority = tasks.count { it.priority == Priority.HIGH }
            val mediumPriority = tasks.count { it.priority == Priority.MEDIUM }
            val lowPriority = tasks.count { it.priority == Priority.LOW }
            val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f

            TaskStats(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                highPriorityTasks = highPriority,
                mediumPriorityTasks = mediumPriority,
                lowPriorityTasks = lowPriority,
                completedPercentage = percentage
            )
        }
    }
}
