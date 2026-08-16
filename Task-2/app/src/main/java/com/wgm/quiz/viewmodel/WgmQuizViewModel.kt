package com.wgm.quiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wgm.quiz.audio.WgmSoundManager
import com.wgm.quiz.audio.WgmSoundManager.WgmSound
import com.wgm.quiz.data.local.WgmScoreRepository
import com.wgm.quiz.domain.repository.WgmQuizRepository
import com.wgm.quiz.ui.components.OptionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class WgmQuizViewModel(
    private val repository: WgmQuizRepository,
    private val soundManager: WgmSoundManager,
    private val scoreRepository: WgmScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WgmGameUiState(
        highScore = scoreRepository.highScore,
        totalCoinsLifetime = scoreRepository.totalCoins,
        gamePhase = GamePhase.Home
    ))
    val uiState: StateFlow<WgmGameUiState> = _uiState.asStateFlow()

    init {
        Log.d("WgmQuizViewModel", "Initializing WgmQuizViewModel with highScore: ${scoreRepository.highScore}, coins: ${scoreRepository.totalCoins}")
    }

    private var timerJob: Job? = null

    fun playClick() {
        soundManager.play(WgmSound.CLICK)
    }

    fun startGameFromHome() {
        Log.d("WgmQuizViewModel", "Starting game from home")
        playClick()
        startGame()
    }

    fun goToHome() {
        playClick()
        timerJob?.cancel()
        soundManager.stopTimerLoop()
        _uiState.update { it.copy(gamePhase = GamePhase.Home) }
    }

    private fun startGame() {
        viewModelScope.launch {
            try {
                Log.d("WgmQuizViewModel", "startGame: Transitioning to Loading phase")
                _uiState.update { it.copy(gamePhase = GamePhase.Loading) }
                Log.d("WgmQuizViewModel", "startGame: Seeding questions if empty...")
                repository.seedQuestionsIfEmpty()
                Log.d("WgmQuizViewModel", "startGame: Questions seeded, loading first question...")
                loadQuestion(1)
            } catch (t: Throwable) {
                Log.e("WgmQuizViewModel", "startGame: Critical error in startGame coroutine", t)
                _uiState.update { it.copy(gamePhase = GamePhase.Home) }
            }
        }
    }

    fun restartGame() {
        Log.d("WgmQuizViewModel", "Restarting game - performing deep reset")
        playClick()
        timerJob?.cancel()
        soundManager.stopTimerLoop()
        
        // Explicitly create a clean initial state
        _uiState.value = WgmGameUiState(
            currentLevel = 1,
            secondsLeft = 30,
            totalScore = 0,
            coinsEarned = 0,
            gamePhase = GamePhase.Loading,
            highScore = scoreRepository.highScore,
            totalCoinsLifetime = scoreRepository.totalCoins,
            lifelines = LifelineType.entries.associateWith { LifelineStatus.AVAILABLE }
        )
        
        startGame()
    }

    private suspend fun loadQuestion(level: Int) {
        Log.d("WgmQuizViewModel", "loadQuestion: Fetching question for level $level")
        val question = repository.getQuestion(level)
        Log.d("WgmQuizViewModel", "loadQuestion: Question found: ${question != null}")
        if (question != null) {
            _uiState.update {
                it.copy(
                    currentQuestion = question,
                    currentLevel = level,
                    secondsLeft = 30,
                    optionStates = List(4) { OptionState.NORMAL },
                    currentPrize = MONEY_LADDER.getOrNull(level - 1) ?: "MAX",
                    showAudiencePoll = false,
                    gamePhase = GamePhase.QuestionActive,
                    isGameOver = false,
                    selectedOptionIndex = -1
                )
            }
            // Play question intro sound
            soundManager.play(WgmSound.QUESTION_INTRO)
            delay(800) // Brief delay for question intro sound
            startTimer()
        } else {
            _uiState.update { it.copy(isGameOver = true, gamePhase = GamePhase.GameOver) }
        }
    }

    // ─── Timer ──────────────────────────────────────────────────────

    private fun startTimer() {
        timerJob?.cancel()
        soundManager.stopTimerLoop()
        
        val totalTime = 30
        _uiState.update { it.copy(secondsLeft = totalTime, isTimerSoundPlaying = true) }
        
        // Start timer loop sound exactly when the logical timer starts
        soundManager.play(WgmSound.TIMER_LOOP)

        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                val remaining = (totalTime - elapsed).toInt()
                
                if (remaining <= 0) {
                    _uiState.update { it.copy(secondsLeft = 0) }
                    break
                }
                
                _uiState.update { it.copy(secondsLeft = remaining) }
                delay(100) // High frequency check for smooth UI and better sync
            }
            onTimeOut()
        }
    }

    private fun onTimeOut() {
        soundManager.stopTimerLoop()
        soundManager.play(WgmSound.TIME_UP)
        _uiState.update {
            it.copy(
                isGameOver = true,
                isTimerSoundPlaying = false,
                gamePhase = GamePhase.GameOver
            )
        }
        finalizeScore()
    }

    // ─── Option Selection ───────────────────────────────────────────

    fun onOptionSelected(index: Int) {
        val currentPhase = _uiState.value.gamePhase
        if (currentPhase != GamePhase.QuestionActive) return

        playClick()
        timerJob?.cancel()
        soundManager.stopTimerLoop()
        _uiState.update { it.copy(isTimerSoundPlaying = false) }

        val question = _uiState.value.currentQuestion ?: return
        val isCorrect = index == question.correctAnswerIndex

        viewModelScope.launch {
            // Phase 1: Lock animation — highlight selection with orange/yellow
            soundManager.play(WgmSound.LOCK)
            _uiState.update { state ->
                state.copy(
                    gamePhase = GamePhase.AnswerLocked,
                    selectedOptionIndex = index,
                    optionStates = state.optionStates.mapIndexed { i, s ->
                        if (i == index) OptionState.SELECTED else s
                    }
                )
            }
            delay(2500) // "Locking" suspense pause

            // Phase 2: Reveal answer
            if (isCorrect) {
                soundManager.play(WgmSound.CORRECT)
                _uiState.update { state ->
                    val levelScore = MONEY_LADDER_VALUES.getOrElse(state.currentLevel - 1) { 0L }
                    val coins = state.currentLevel * 100
                    state.copy(
                        gamePhase = GamePhase.CorrectReveal,
                        optionStates = state.optionStates.mapIndexed { i, _ ->
                            if (i == question.correctAnswerIndex) OptionState.CORRECT else OptionState.NORMAL
                        },
                        totalScore = state.totalScore + levelScore,
                        coinsEarned = state.coinsEarned + coins
                    )
                }
                delay(2000)

                if (_uiState.value.currentLevel == 16) {
                    // Won Grand Jackpot!
                    _uiState.update { it.copy(
                        isGameOver = true,
                        lastWonAmount = MONEY_LADDER.last(),
                        gamePhase = GamePhase.Victory
                    )}
                    finalizeScore()
                } else {
                    loadQuestion(_uiState.value.currentLevel + 1)
                }
            } else {
                soundManager.play(WgmSound.WRONG)
                _uiState.update { state ->
                    state.copy(
                        gamePhase = GamePhase.WrongReveal,
                        optionStates = state.optionStates.mapIndexed { i, _ ->
                            when {
                                i == question.correctAnswerIndex -> OptionState.CORRECT
                                i == index -> OptionState.WRONG
                                else -> OptionState.NORMAL
                            }
                        }
                    )
                }
                delay(2000)
                handleWrongAnswer()
            }
        }
    }

    private fun handleWrongAnswer() {
        val canUseExtraLife = _uiState.value.lifelines[LifelineType.EXTRA_LIFE] == LifelineStatus.AVAILABLE
        if (canUseExtraLife) {
            _uiState.update { it.copy(showExtraLifeDialog = true) }
        } else {
            gameOver()
        }
    }

    private fun gameOver() {
        val currentLevel = _uiState.value.currentLevel
        val wonAmount = calculateSafeHavenPrize(currentLevel)
        _uiState.update { it.copy(
            isGameOver = true,
            lastWonAmount = wonAmount,
            gamePhase = GamePhase.GameOver
        )}
        finalizeScore()
    }

    private fun calculateSafeHavenPrize(level: Int): String {
        return when {
            level > 10 -> MONEY_LADDER[9]
            level > 5 -> MONEY_LADDER[4]
            else -> "₹ 0"
        }
    }

    private fun finalizeScore() {
        val state = _uiState.value
        scoreRepository.onGameFinished(
            score = state.totalScore,
            coins = state.coinsEarned,
            levelReached = state.currentLevel
        )
        _uiState.update {
            it.copy(
                highScore = scoreRepository.highScore,
                totalCoinsLifetime = scoreRepository.totalCoins
            )
        }
    }

    // ─── Lifeline Actions ───────────────────────────────────────────

    fun useFiftyFifty() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.QuestionActive) return
        val question = state.currentQuestion ?: return
        if (state.lifelines[LifelineType.FIFTY_FIFTY] != LifelineStatus.AVAILABLE) return

        val incorrectIndices = (0..3).filter { it != question.correctAnswerIndex }.shuffled().take(2)
        _uiState.update { s ->
            s.copy(
                optionStates = s.optionStates.mapIndexed { i, os ->
                    if (incorrectIndices.contains(i)) OptionState.HIDDEN else os
                },
                lifelines = s.lifelines + (LifelineType.FIFTY_FIFTY to LifelineStatus.USED)
            )
        }
    }

    fun useAudiencePoll() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.QuestionActive) return
        val question = state.currentQuestion ?: return
        if (state.lifelines[LifelineType.AUDIENCE_POLL] != LifelineStatus.AVAILABLE) return

        // Weighted probability: higher difficulty = less audience accuracy
        val pollData = mutableMapOf<Int, Int>()
        var remaining = 100
        val correctIndex = question.correctAnswerIndex
        val difficultyFactor = (16 - question.difficulty).coerceIn(1, 15)

        val correctShare = Random.nextInt(
            25 + difficultyFactor * 2,
            45 + difficultyFactor * 2
        ).coerceAtMost(85)
        pollData[correctIndex] = correctShare
        remaining -= correctShare

        val others = (0..3).filter { it != correctIndex }
        others.dropLast(1).forEach {
            val share = if (remaining > 0) Random.nextInt(0, remaining) else 0
            pollData[it] = share
            remaining -= share
        }
        pollData[others.last()] = remaining

        _uiState.update { s ->
            s.copy(
                showAudiencePoll = true,
                audiencePollData = pollData,
                lifelines = s.lifelines + (LifelineType.AUDIENCE_POLL to LifelineStatus.USED)
            )
        }
    }

    fun dismissAudiencePoll() {
        _uiState.update { it.copy(showAudiencePoll = false) }
    }

    fun useFlip() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.QuestionActive) return
        if (state.lifelines[LifelineType.FLIP] != LifelineStatus.AVAILABLE) return

        viewModelScope.launch {
            val currentQ = state.currentQuestion ?: return@launch
            val newQ = repository.getAlternativeQuestion(state.currentLevel, currentQ.id)
            if (newQ != null) {
                _uiState.update { s ->
                    s.copy(
                        currentQuestion = newQ,
                        optionStates = List(4) { OptionState.NORMAL },
                        lifelines = s.lifelines + (LifelineType.FLIP to LifelineStatus.USED)
                    )
                }
            }
        }
    }

    fun useExtraLife() {
        // Extra Life: keep the same question but reset option states and timer
        val currentQuestion = _uiState.value.currentQuestion
        _uiState.update { s ->
            s.copy(
                showExtraLifeDialog = false,
                lifelines = s.lifelines + (LifelineType.EXTRA_LIFE to LifelineStatus.USED),
                optionStates = List(4) { OptionState.NORMAL },
                secondsLeft = 30,
                gamePhase = GamePhase.QuestionActive,
                selectedOptionIndex = -1
            )
        }
        startTimer()
    }

    fun dismissExtraLife() {
        _uiState.update { it.copy(showExtraLifeDialog = false) }
        gameOver()
    }

    fun toggleMoneyLadder() {
        _uiState.update { it.copy(showMoneyLadder = !it.showMoneyLadder) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.stopTimerLoop()
    }
}
