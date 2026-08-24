package com.deshnews.app.domain.usecase

import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case: retrieves the current stream of top headlines.
 *
 * Follows the **Interactor** pattern — a single-responsibility callable that wraps
 * the repository so the ViewModel has no direct dependency on the data layer.
 *
 * Usage:
 * ```kotlin
 * val articlesFlow: Flow<List<NewsArticle>> = getHeadlinesUseCase()
 * ```
 */
class GetHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(category: String = "general"): Flow<List<NewsArticle>> =
        repository.getHeadlines(category)
}
