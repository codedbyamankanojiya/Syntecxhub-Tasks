package com.deshnews.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached news article.
 *
 * The [url] is the natural unique identifier provided by the GNews API.
 * [isBookmarked] persists across cache evictions; [cachedAt] drives TTL eviction.
 */
@Entity(tableName = "news_articles")
data class NewsEntity(
    /** Unique article URL — used as primary key. */
    @PrimaryKey
    val url: String,

    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,

    /** ISO-8601 timestamp string from the API, e.g. "2025-05-25T08:45:00Z". */
    val publishedAt: String,

    val sourceName: String,
    val sourceUrl: String,

    /** Category this article was fetched for. */
    val category: String = "general",

    /** User-persisted bookmark; not cleared during cache eviction. */
    val isBookmarked: Boolean = false,

    /** Unix epoch millis at time of insertion — drives TTL queries. */
    val cachedAt: Long = System.currentTimeMillis()
)
