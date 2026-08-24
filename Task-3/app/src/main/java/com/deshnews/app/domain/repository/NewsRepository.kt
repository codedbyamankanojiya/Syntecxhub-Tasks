package com.deshnews.app.domain.repository

import com.deshnews.app.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer contract for all news data operations.
 *
 * Implementations must follow an **offline-first** strategy:
 *  1. Immediately emit whatever is in the local Room cache.
 *  2. In the background, fetch fresh data from the network and merge it into Room.
 *  3. The [Flow] returned by [getHeadlines] will automatically emit again once Room updates.
 */
interface NewsRepository {

    /**
     * Returns a cold [Flow] backed by Room. The first emission contains cached articles
     * (possibly empty on first launch); subsequent emissions reflect network refreshes.
     *
     * @param category Optional category to filter by (default "general")
     */
    fun getHeadlines(category: String = "general"): Flow<List<NewsArticle>>

    /**
     * Triggers an explicit network refresh of top-headlines and caches the result.
     *
     * @param category Optional category to refresh (e.g. general, technology, sports)
     * @return [Result.success] on success, [Result.failure] wrapping the cause on error.
     */
    suspend fun refreshHeadlines(category: String = "general"): Result<Unit>

    /**
     * Reactive stream of articles the user has bookmarked.
     * Emits whenever the bookmark state changes in Room.
     */
    fun getBookmarkedArticles(): Flow<List<NewsArticle>>

    /**
     * Persists the bookmark state of an article by its [url].
     */
    suspend fun toggleBookmark(url: String, isBookmarked: Boolean)

    /**
     * Returns a single article from the Room cache by its URL.
     * Returns `null` if the article has been evicted.
     */
    suspend fun getArticleByUrl(url: String): NewsArticle?
}
