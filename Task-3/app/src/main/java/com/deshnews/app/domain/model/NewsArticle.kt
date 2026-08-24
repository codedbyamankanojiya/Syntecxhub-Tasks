package com.deshnews.app.domain.model

/**
 * Clean domain model representing a single news article.
 *
 * This is the only article type that should cross the domain/presentation boundary.
 * Data-layer DTOs ([com.deshnews.app.data.remote.dto.ArticleDto]) and
 * Room entities ([com.deshnews.app.data.local.NewsEntity]) are mapped to this model
 * inside [com.deshnews.app.data.repository.NewsRepositoryImpl].
 */
data class NewsArticle(
    /** Globally unique article URL — used as navigation key and Room primary key. */
    val url: String,

    val title: String,
    val description: String,

    /** Full article body. Falls back to [description] if the API truncates it. */
    val content: String,

    /** Absolute URL of the article's cover image. Empty string if unavailable. */
    val imageUrl: String,

    /** ISO-8601 datetime string as returned by the API ("2025-05-25T08:45:00Z"). */
    val publishedAt: String,

    val sourceName: String,
    val sourceUrl: String,

    val category: String = "general",

    val isBookmarked: Boolean = false
)
