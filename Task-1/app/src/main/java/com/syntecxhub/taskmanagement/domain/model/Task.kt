package com.syntecxhub.taskmanagement.domain.model

import java.time.LocalDateTime

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: LocalDateTime,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
