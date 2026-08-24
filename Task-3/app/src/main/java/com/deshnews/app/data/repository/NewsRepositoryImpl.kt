package com.deshnews.app.data.repository

import com.deshnews.app.BuildConfig
import com.deshnews.app.data.local.NewsDao
import com.deshnews.app.data.local.NewsEntity
import com.deshnews.app.data.remote.NewsApiService
import com.deshnews.app.data.remote.dto.ArticleDto
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [NewsRepository].
 *
 * Strategy:
 *  1. [getHeadlines] immediately returns a reactive [Flow] backed by Room.
 *  2. [refreshHeadlines] fetches from GNews API, preserves bookmarks, and merges into Room.
 *  3. The UI reacts to Room changes automatically — no manual cache invalidation required.
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val newsDao: NewsDao
) : NewsRepository {

    // ── Read ───────────────────────────────────────────────────────────────────

    override fun getHeadlines(category: String): Flow<List<NewsArticle>> =
        newsDao.getArticlesByCategory(category).map { entities -> entities.map { it.toDomain() } }

    override fun getBookmarkedArticles(): Flow<List<NewsArticle>> =
        newsDao.getBookmarkedArticles().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getArticleByUrl(url: String): NewsArticle? =
        newsDao.getArticleByUrl(url)?.toDomain()

    // ── Write ──────────────────────────────────────────────────────────────────

    override suspend fun refreshHeadlines(category: String): Result<Unit> = runCatching {
        val response = apiService.getTopHeadlines(
            token = BuildConfig.GNEWS_API_KEY,
            category = category
        )

        // Snapshot existing bookmarks so we don't lose them on REPLACE
        val bookmarkedUrls = newsDao
            .getArticlesSnapshot()
            .filter { it.isBookmarked }
            .map { it.url }
            .toSet()

        val entities = response.articles.map { dto ->
            dto.toEntity(
                category = category,
                isBookmarked = dto.url in bookmarkedUrls
            )
        }
        newsDao.insertAll(entities)
    }

    override suspend fun toggleBookmark(url: String, isBookmarked: Boolean) {
        newsDao.updateBookmark(url, isBookmarked)
    }

    // ── Mappers ────────────────────────────────────────────────────────────────

    private fun NewsEntity.toDomain() = NewsArticle(
        url          = url,
        title        = title,
        description  = description.orEmpty(),
        content      = content ?: description.orEmpty(),
        imageUrl     = imageUrl.orEmpty(),
        publishedAt  = publishedAt,
        sourceName   = sourceName,
        sourceUrl    = sourceUrl,
        category     = category,
        isBookmarked = isBookmarked
    )

    private fun ArticleDto.toEntity(category: String, isBookmarked: Boolean = false) = NewsEntity(
        url          = url,
        title        = title,
        description  = description,
        content      = content,
        imageUrl     = image,
        publishedAt  = publishedAt,
        sourceName   = source.name,
        sourceUrl    = source.url,
        category     = category,
        isBookmarked = isBookmarked,
        cachedAt     = System.currentTimeMillis()
    )
}
