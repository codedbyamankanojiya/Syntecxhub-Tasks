package com.deshnews.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for the [NewsEntity] table.
 *
 * All mutating operations are `suspend` functions; read operations exposed
 * as [Flow] to support reactive UI updates via StateFlow in the ViewModel.
 */
@Dao
interface NewsDao {

    // ── Write ──────────────────────────────────────────────────────────────────

    /**
     * Inserts or replaces a batch of articles.
     * REPLACE strategy updates the cache while preserving [NewsEntity.isBookmarked] state
     * (isBookmarked is overwritten, so the caller must preserve it before calling this).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: NewsEntity)

    // ── Read ───────────────────────────────────────────────────────────────────

    /** Reactive stream of all articles ordered newest-first. */
    @Query("SELECT * FROM news_articles ORDER BY cachedAt DESC")
    fun getAllArticles(): Flow<List<NewsEntity>>

    /** Reactive stream of articles by category. */
    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY cachedAt DESC")
    fun getArticlesByCategory(category: String): Flow<List<NewsEntity>>

    /** One-shot snapshot for prefetching; useful in the repository impl. */
    @Query("SELECT * FROM news_articles ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getArticlesSnapshot(limit: Int = 30): List<NewsEntity>

    /** Look up a single article by URL (for the detail screen). */
    @Query("SELECT * FROM news_articles WHERE url = :url LIMIT 1")
    suspend fun getArticleByUrl(url: String): NewsEntity?

    /** Reactive stream of bookmarked articles. */
    @Query("SELECT * FROM news_articles WHERE isBookmarked = 1 ORDER BY cachedAt DESC")
    fun getBookmarkedArticles(): Flow<List<NewsEntity>>

    // ── Update ─────────────────────────────────────────────────────────────────

    @Query("UPDATE news_articles SET isBookmarked = :isBookmarked WHERE url = :url")
    suspend fun updateBookmark(url: String, isBookmarked: Boolean)

    // ── Delete / Eviction ──────────────────────────────────────────────────────

    /** Remove non-bookmarked articles older than [olderThanMillis]. */
    @Query("DELETE FROM news_articles WHERE isBookmarked = 0 AND cachedAt < :olderThanMillis")
    suspend fun evictStaleArticles(olderThanMillis: Long)

    /** Wipe the entire non-bookmarked cache (used before a fresh API fetch). */
    @Query("DELETE FROM news_articles WHERE isBookmarked = 0")
    suspend fun clearCache()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun count(): Int
}
