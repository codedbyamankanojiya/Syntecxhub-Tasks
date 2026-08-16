package com.wgm.quiz.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences-backed repository for lightweight score persistence across sessions.
 */
class WgmScoreRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    var highScore: Long
        get() = prefs.getLong(KEY_HIGH_SCORE, 0L)
        set(value) = prefs.edit().putLong(KEY_HIGH_SCORE, value).apply()

    var totalCoins: Int
        get() = prefs.getInt(KEY_TOTAL_COINS, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_COINS, value).apply()

    var gamesPlayed: Int
        get() = prefs.getInt(KEY_GAMES_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_GAMES_PLAYED, value).apply()

    var bestLevel: Int
        get() = prefs.getInt(KEY_BEST_LEVEL, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_LEVEL, value).apply()

    /**
     * Update stats after a game ends.
     */
    fun onGameFinished(score: Long, coins: Int, levelReached: Int) {
        if (score > highScore) highScore = score
        if (levelReached > bestLevel) bestLevel = levelReached
        totalCoins += coins
        gamesPlayed += 1
    }

    companion object {
        private const val PREFS_NAME = "wgm_scores"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_TOTAL_COINS = "total_coins"
        private const val KEY_GAMES_PLAYED = "games_played"
        private const val KEY_BEST_LEVEL = "best_level"
    }
}
