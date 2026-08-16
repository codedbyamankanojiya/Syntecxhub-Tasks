package com.wgm.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wgm.quiz.audio.WgmSoundManager
import com.wgm.quiz.data.local.WgmScoreRepository
import com.wgm.quiz.domain.repository.WgmQuizRepository

class WgmQuizViewModelFactory(
    private val repository: WgmQuizRepository,
    private val soundManager: WgmSoundManager,
    private val scoreRepository: WgmScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WgmQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WgmQuizViewModel(repository, soundManager, scoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
