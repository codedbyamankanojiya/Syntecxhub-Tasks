package com.deshnews.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.domain.repository.NewsRepository
import com.deshnews.app.domain.usecase.GetHeadlinesUseCase
import com.deshnews.app.presentation.state.DetailUiState
import com.deshnews.app.presentation.state.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getHeadlinesUseCase: GetHeadlinesUseCase,
    private val repository: NewsRepository
) : ViewModel() {

    // ── Home screen state ──────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("general")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // ── Theme state ────────────────────────────────────────────────────────────

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // ── Detail screen state ────────────────────────────────────────────────────

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    // ── Bookmarks ──────────────────────────────────────────────────────────────

    private val _bookmarkedArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val bookmarkedArticles: StateFlow<List<NewsArticle>> = _bookmarkedArticles.asStateFlow()

    // ── Events (One-time actions like Toasts/Snackbars) ───────────────────────

    private val _errorEvent = Channel<String>()
    val errorEvent = _errorEvent.receiveAsFlow()

    private var headlineJob: Job? = null

    init {
        observeHeadlines()
        triggerInitialRefresh()
        observeBookmarks()
    }

    // ── Intent: observe room stream ────────────────────────────────────────────

    private fun observeHeadlines() {
        headlineJob?.cancel()
        headlineJob = getHeadlinesUseCase(_selectedCategory.value)
            .onEach { articles ->
                _uiState.update { current ->
                    if (articles.isNotEmpty()) {
                        NewsUiState.Success(
                            headlines      = articles,
                            isRefreshing   = current is NewsUiState.Success &&
                                             (current as NewsUiState.Success).isRefreshing
                        )
                    } else {
                        if (current is NewsUiState.Loading) current else current
                    }
                }
            }
            .catch { e ->
                _uiState.update { current ->
                    val cached = (current as? NewsUiState.Success)?.headlines ?: emptyList()
                    NewsUiState.Error(
                        message        = e.message ?: "An unexpected error occurred",
                        cachedArticles = cached
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Intent: update category ───────────────────────────────────────────────

    fun setCategory(category: String) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        observeHeadlines()
        refreshHeadlines()
    }

    // ── Intent: theme toggle ──────────────────────────────────────────────────

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // ── Intent: initial network fetch ─────────────────────────────────────────

    private fun triggerInitialRefresh() {
        viewModelScope.launch {
            val result = repository.refreshHeadlines(_selectedCategory.value)
            if (result.isFailure && _uiState.value is NewsUiState.Loading) {
                _uiState.update {
                    NewsUiState.Error(
                        message = result.exceptionOrNull()?.message ?: "Failed to load news",
                        cachedArticles = emptyList()
                    )
                }
            } else if (result.isFailure) {
                // If we already have data, just notify via event
                _errorEvent.send(result.exceptionOrNull()?.message ?: "Refresh failed")
            }
        }
    }

    // ── Intent: pull-to-refresh ────────────────────────────────────────────────

    fun refreshHeadlines() {
        viewModelScope.launch {
            _uiState.update { current ->
                when (current) {
                    is NewsUiState.Success -> current.copy(isRefreshing = true)
                    else                   -> NewsUiState.Loading
                }
            }

            val result = repository.refreshHeadlines(_selectedCategory.value)

            if (result.isFailure) {
                val errorMessage = result.exceptionOrNull()?.message ?: "Failed to refresh"
                _uiState.update { current ->
                    val cached = (current as? NewsUiState.Success)?.headlines ?: emptyList()
                    if (cached.isEmpty()) {
                        NewsUiState.Error(message = errorMessage)
                    } else {
                        NewsUiState.Success(headlines = cached, isRefreshing = false)
                    }
                }
                // Notify user via event so they know WHY it didn't refresh
                _errorEvent.send(errorMessage)
            }
        }
    }

    // ── Intent: load article detail ────────────────────────────────────────────

    fun loadArticleDetail(url: String) {
        viewModelScope.launch {
            _detailUiState.update { DetailUiState.Loading }

            val article = repository.getArticleByUrl(url)
            if (article == null) {
                _detailUiState.update { DetailUiState.Error("Article not found in cache") }
                return@launch
            }

            val related = buildRelatedArticles(article)
            _detailUiState.update { DetailUiState.Success(article, related) }
        }
    }

    // ── Intent: toggle bookmark ────────────────────────────────────────────────

    fun toggleBookmark(url: String) {
        viewModelScope.launch {
            val article = repository.getArticleByUrl(url) ?: return@launch
            repository.toggleBookmark(url, !article.isBookmarked)
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun observeBookmarks() {
        repository.getBookmarkedArticles()
            .onEach { articles -> _bookmarkedArticles.update { articles } }
            .catch { /* silently ignore bookmark stream errors */ }
            .launchIn(viewModelScope)
    }

    private fun buildRelatedArticles(current: NewsArticle): List<NewsArticle> {
        val allArticles = (_uiState.value as? NewsUiState.Success)?.headlines ?: return emptyList()
        return allArticles
            .filter { it.url != current.url }
            .take(6)
    }
}
