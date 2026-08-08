package com.syntecxhub.taskmanagement.data.local

import androidx.room.*
import com.syntecxhub.taskmanagement.domain.model.Priority
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalTaskCount(): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    suspend fun getCompletedTaskCount(): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = :priority")
    suspend fun getTaskCountByPriority(priority: Priority): Int
}
