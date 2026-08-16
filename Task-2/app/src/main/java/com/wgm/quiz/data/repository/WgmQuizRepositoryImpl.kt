package com.wgm.quiz.data.repository

import com.wgm.quiz.data.local.WgmQuestionDao
import com.wgm.quiz.data.local.WgmQuestionEntity
import com.wgm.quiz.domain.model.WgmQuestion
import com.wgm.quiz.domain.repository.WgmQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WgmQuizRepositoryImpl(
    private val dao: WgmQuestionDao
) : WgmQuizRepository {

    override suspend fun getQuestion(difficulty: Int): WgmQuestion? = withContext(Dispatchers.IO) {
        dao.getRandomQuestionByDifficulty(difficulty)?.toDomain()
    }

    override suspend fun getAlternativeQuestion(difficulty: Int, excludeId: Long): WgmQuestion? = withContext(Dispatchers.IO) {
        dao.getAlternativeQuestion(difficulty, excludeId)?.toDomain()
    }

    override suspend fun seedQuestionsIfEmpty() = withContext(Dispatchers.IO) {
        val count = dao.getQuestionCount()
        android.util.Log.d("WgmQuizRepositoryImpl", "seedQuestionsIfEmpty: Current question count in DB = $count")
        if (count == 0) {
            android.util.Log.d("WgmQuizRepositoryImpl", "seedQuestionsIfEmpty: Seeding DB with sample questions")
            val sampleQuestions = mutableListOf<WgmQuestionEntity>()
            // Generate 3 sample questions for each of the 15 levels
            for (level in 1..15) {
                // ... (keeping existing logic for brevity or explicitly replacing)
                sampleQuestions.add(
                    WgmQuestionEntity(
                        text = "Sample Question for Level $level: What is the capital of Techland?",
                        optionA = "A: Binary City",
                        optionB = "B: Silicon Valley",
                        optionC = "C: Kernel Square",
                        optionD = "D: Stack Overflow",
                        correctAnswerIndex = (level % 4),
                        difficulty = level
                    )
                )
                sampleQuestions.add(
                    WgmQuestionEntity(
                        text = "Another Level $level Question: Which planet is closest to the Sun?",
                        optionA = "A: Venus",
                        optionB = "B: Mercury",
                        optionC = "C: Mars",
                        optionD = "D: Earth",
                        correctAnswerIndex = 1,
                        difficulty = level
                    )
                )
            }
            android.util.Log.d("WgmQuizRepositoryImpl", "seedQuestionsIfEmpty: Inserting ${sampleQuestions.size} questions")
            dao.insertQuestions(sampleQuestions)
            android.util.Log.d("WgmQuizRepositoryImpl", "seedQuestionsIfEmpty: Insertion complete")
        }
    }

    private fun WgmQuestionEntity.toDomain(): WgmQuestion {
        return WgmQuestion(
            id = id,
            text = text,
            options = listOf(optionA, optionB, optionC, optionD),
            correctAnswerIndex = correctAnswerIndex,
            difficulty = difficulty
        )
    }
}
