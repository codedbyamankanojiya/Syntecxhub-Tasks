package com.deshnews.app.data.remote

import com.deshnews.app.data.remote.dto.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the GNews REST API.
 *
 * Base URL: https://gnews.io/api/
 *
 * Free-tier supports up to 100 requests/day with up to 10 articles per request.
 * Obtain your key at: https://gnews.io
 */
interface NewsApiService {

    /**
     * Fetches top headlines for the given [country] and optional [category].
     *
     * @param token    GNews API key (injected via BuildConfig.GNEWS_API_KEY).
     * @param lang     Language code, default "en".
     * @param country  Country code, default "in" (India).
     * @param max      Max articles per page (1–10 on free tier).
     * @param category One of: general, world, nation, business, technology,
     *                 entertainment, sports, science, health.
     */
    @GET("v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("token")    token: String,
        @Query("lang")     lang: String    = "en",
        @Query("country")  country: String = "in",
        @Query("max")      max: Int        = 10,
        @Query("category") category: String = "general"
    ): GNewsResponse

    /**
     * Full-text search across all indexed articles.
     *
     * @param query   The search keywords.
     * @param sortBy  "publishedAt" (newest) or "relevance".
     */
    @GET("v4/search")
    suspend fun searchNews(
        @Query("q")      query: String,
        @Query("token")  token: String,
        @Query("lang")   lang: String   = "en",
        @Query("max")    max: Int       = 10,
        @Query("sortby") sortBy: String = "publishedAt"
    ): GNewsResponse

    companion object {
        /** Base URL for all GNews API calls — trailing slash is required by Retrofit. */
        const val BASE_URL = "https://gnews.io/api/"
    }
}
