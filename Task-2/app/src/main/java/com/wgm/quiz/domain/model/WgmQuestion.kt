package com.wgm.quiz.domain.model

data class WgmQuestion(
    val id: Long,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val difficulty: Int
)
