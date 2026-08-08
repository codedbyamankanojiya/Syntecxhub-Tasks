package com.syntecxhub.taskmanagement.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syntecxhub.taskmanagement.domain.model.Priority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean,
    val reminderEnabled: Boolean,
    val createdAt: Long
)
