package com.wgm.quiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WgmQuestionDao {
    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestionByDifficulty(difficulty: Int): WgmQuestionEntity?

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty AND id != :excludeId ORDER BY RANDOM() LIMIT 1")
    suspend fun getAlternativeQuestion(difficulty: Int, excludeId: Long): WgmQuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<WgmQuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}
