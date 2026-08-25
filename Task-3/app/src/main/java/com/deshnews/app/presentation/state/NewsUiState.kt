package com.deshnews.app.presentation.state

import com.deshnews.app.domain.model.NewsArticle

/**
 * Sealed hierarchy representing every possible UI state for the home screen.
 *
 * Consumed by [com.deshnews.app.presentation.viewmodel.NewsViewModel] and
 * observed in [com.deshnews.app.presentation.ui.screen.NewsHomeScreen].
 */
sealed class NewsUiState {

    /** Initial state — data is being loaded for the first time. */
    object Loading : NewsUiState()

    /**
     * Articles are available.
     *
     * @param headlines      Full ordered list of articles.
     * @param featuredArticles Top-N articles shown in the carousel (default first 5).
     * @param isRefreshing   True while a pull-to-refresh network call is in flight.
     */
    data class Success(
        val headlines: List<NewsArticle>,
        val featuredArticles: List<NewsArticle> = headlines.take(5),
        val isRefreshing: Boolean = false,
        val isSearch: Boolean = false,
        val searchQuery: String = ""
    ) : NewsUiState()

    /**
     * A fatal error occurred. [cachedArticles] may contain stale data to show
     * an offline fallback instead of a blank screen.
     */
    data class Error(
        val message: String,
        val cachedArticles: List<NewsArticle> = emptyList()
    ) : NewsUiState()
}

/**
 * Sealed hierarchy for the article detail screen.
 */
sealed class DetailUiState {

    object Loading : DetailUiState()

    data class Success(
        val article: NewsArticle,
        val relatedArticles: List<NewsArticle> = emptyList()
    ) : DetailUiState()

    data class Error(val message: String) : DetailUiState()
}
