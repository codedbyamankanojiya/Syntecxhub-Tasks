package com.wgm.quiz

import android.app.Application
import androidx.room.Room
import com.wgm.quiz.audio.WgmSoundManager
import com.wgm.quiz.data.local.WgmDatabase
import com.wgm.quiz.data.local.WgmScoreRepository
import com.wgm.quiz.data.repository.WgmQuizRepositoryImpl
import com.wgm.quiz.domain.repository.WgmQuizRepository

class WgmApplication : Application() {

    private val database by lazy {
        Room.databaseBuilder(
            this,
            WgmDatabase::class.java,
            "wgm_database"
        ).fallbackToDestructiveMigration()
         .build()
    }

    val repository: WgmQuizRepository by lazy {
        WgmQuizRepositoryImpl(database.wgmQuestionDao())
    }

    val soundManager: WgmSoundManager by lazy {
        WgmSoundManager(this)
    }

    val scoreRepository: WgmScoreRepository by lazy {
        WgmScoreRepository(this)
    }
}
