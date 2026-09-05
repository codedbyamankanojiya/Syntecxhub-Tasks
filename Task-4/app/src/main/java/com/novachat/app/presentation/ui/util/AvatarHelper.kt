package com.novachat.app.presentation.ui.util

import java.util.UUID

/**
 * Helper to generate randomized, high-quality avatar URLs using the DiceBear public API.
 */
object AvatarHelper {

    private val STYLES = listOf(
        "avataaars",
        "bottts",
        "lorelei",
        "adventurer",
        "fun-emoji",
        "micah",
        "personas"
    )

    private val SEED_WORDS = listOf(
        "Felix", "Luna", "Milo", "Oliver", "Leo", "Maya", "Nova", "Jasper",
        "Chloe", "Finn", "Ruby", "Oscar", "Zoe", "Sam", "Kai", "Zara",
        "Pixel", "Cosmo", "Echo", "Atlas", "Shadow", "Blaze", "Frost", "Aura"
    )

    /**
     * Generates a single randomized avatar URL.
     */
    fun getRandomAvatarUrl(style: String? = null): String {
        val selectedStyle = style ?: STYLES.random()
        val seed = UUID.randomUUID().toString().take(8)
        return "https://api.dicebear.com/7.x/$selectedStyle/png?seed=$seed&size=128"
    }

    /**
     * Generates a batch of randomized avatar URLs across diverse styles.
     */
    fun generateRandomAvatars(count: Int = 12): List<String> {
        val shuffledSeeds = (SEED_WORDS + List(count) { UUID.randomUUID().toString().take(6) }).shuffled()
        return (0 until count).map { i ->
            val style = STYLES[i % STYLES.size]
            val seed = shuffledSeeds.getOrElse(i) { "seed_$i" }
            "https://api.dicebear.com/7.x/$style/png?seed=$seed&size=128"
        }
    }
}
