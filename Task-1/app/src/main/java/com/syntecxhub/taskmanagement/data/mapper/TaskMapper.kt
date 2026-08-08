package com.syntecxhub.taskmanagement.data.mapper

import com.syntecxhub.taskmanagement.data.local.TaskEntity
import com.syntecxhub.taskmanagement.domain.model.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = priority,
        dueDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault()),
        isCompleted = isCompleted,
        reminderEnabled = reminderEnabled,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority,
        dueDate = dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isCompleted = isCompleted,
        reminderEnabled = reminderEnabled,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
