package com.syntecxhub.taskmanagement.domain.model

data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val highPriorityTasks: Int,
    val mediumPriorityTasks: Int,
    val lowPriorityTasks: Int,
    val completedPercentage: Float
)
