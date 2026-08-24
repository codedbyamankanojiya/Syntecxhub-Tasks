package com.deshnews.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level GNews API response envelope.
 * Endpoint: GET https://gnews.io/api/v4/top-headlines
 */
@Serializable
data class GNewsResponse(
    @SerialName("totalArticles") val totalArticles: Int = 0,
    @SerialName("articles")      val articles: List<ArticleDto> = emptyList()
)

/**
 * Individual article returned by GNews.
 */
@Serializable
data class ArticleDto(
    @SerialName("title")       val title: String,
    @SerialName("description") val description: String?   = null,
    @SerialName("content")     val content: String?       = null,
    @SerialName("url")         val url: String,
    @SerialName("image")       val image: String?         = null,
    @SerialName("publishedAt") val publishedAt: String    = "",
    @SerialName("source")      val source: SourceDto      = SourceDto()
)

/**
 * News source metadata embedded in each article.
 */
@Serializable
data class SourceDto(
    @SerialName("name") val name: String = "",
    @SerialName("url")  val url: String  = ""
)
