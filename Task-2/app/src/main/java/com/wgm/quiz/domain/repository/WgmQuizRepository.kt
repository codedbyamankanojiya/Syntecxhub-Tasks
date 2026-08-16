package com.wgm.quiz.domain.repository

import com.wgm.quiz.domain.model.WgmQuestion

interface WgmQuizRepository {
    suspend fun getQuestion(difficulty: Int): WgmQuestion?
    suspend fun getAlternativeQuestion(difficulty: Int, excludeId: Long): WgmQuestion?
    suspend fun seedQuestionsIfEmpty()
}
