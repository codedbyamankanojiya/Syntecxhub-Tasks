package com.wgm.quiz.viewmodel

import com.wgm.quiz.domain.model.WgmQuestion
import com.wgm.quiz.ui.components.OptionState

/**
 * Sealed class representing the distinct phases of the game state machine.
 * Replaces scattered boolean flags with a single, type-safe state.
 */
sealed class GamePhase {
    /** App is on Home screen */
    object Home : GamePhase()
    /** Initial loading / seeding database */
    object Loading : GamePhase()
    /** A question is actively being answered; timer is running */
    object QuestionActive : GamePhase()
    /** Player has locked an answer; waiting for reveal */
    object AnswerLocked : GamePhase()
    /** Correct answer revealed with celebration */
    object CorrectReveal : GamePhase()
    /** Wrong answer revealed */
    object WrongReveal : GamePhase()
    /** Game is over — player lost or quit */
    object GameOver : GamePhase()
    /** Player won the grand jackpot! */
    object Victory : GamePhase()
}

data class WgmGameUiState(
    val currentQuestion: WgmQuestion? = null,
    val currentLevel: Int = 1,
    val secondsLeft: Int = 30,
    val optionStates: List<OptionState> = listOf(OptionState.NORMAL, OptionState.NORMAL, OptionState.NORMAL, OptionState.NORMAL),
    val lifelines: Map<LifelineType, LifelineStatus> = LifelineType.values().associateWith { LifelineStatus.AVAILABLE },
    val showAudiencePoll: Boolean = false,
    val audiencePollData: Map<Int, Int> = emptyMap(),
    val showExtraLifeDialog: Boolean = false,
    val isGameOver: Boolean = false,
    val lastWonAmount: String = "₹ 0",
    val currentPrize: String = "₹ 1,000",
    val showMoneyLadder: Boolean = false,
    // ── New fields ──
    val gamePhase: GamePhase = GamePhase.Loading,
    val totalScore: Long = 0,
    val coinsEarned: Int = 0,
    val highScore: Long = 0,
    val totalCoinsLifetime: Int = 0,
    val isTimerSoundPlaying: Boolean = false,
    val selectedOptionIndex: Int = -1
)

enum class LifelineType {
    FIFTY_FIFTY, AUDIENCE_POLL, FLIP, EXTRA_LIFE
}

enum class LifelineStatus {
    AVAILABLE, USED, HIDDEN
}

val MONEY_LADDER = listOf(
    "₹ 1,000", "₹ 2,000", "₹ 3,000", "₹ 5,000", "₹ 10,000", // Milestone 1
    "₹ 20,000", "₹ 40,000", "₹ 80,000", "₹ 1,60,000", "₹ 3,20,000", // Milestone 2
    "₹ 6,40,000", "₹ 12,50,000", "₹ 25,00,000", "₹ 50,00,000", "₹ 1 Crore", "₹ 7 Crores"
)

// Prize values in numeric form for score calculation
val MONEY_LADDER_VALUES = listOf(
    1000L, 2000L, 3000L, 5000L, 10000L,
    20000L, 40000L, 80000L, 160000L, 320000L,
    640000L, 1250000L, 2500000L, 5000000L, 10000000L, 70000000L
)

val SAFE_HAVENS = listOf(5, 10, 16)
