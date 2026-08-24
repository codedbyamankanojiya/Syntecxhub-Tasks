package com.deshnews.app.presentation.ui.screen

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.presentation.state.NewsUiState
import com.deshnews.app.presentation.ui.components.BreakingNewsBadge
import com.deshnews.app.presentation.ui.components.HeadlineCard
import com.deshnews.app.presentation.ui.components.StudioBannerCarousel
import com.deshnews.app.presentation.ui.theme.BroadcastRed
import com.deshnews.app.presentation.ui.theme.StudioGold
import com.deshnews.app.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewsHomeScreen(
    onArticleClick: (String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedArticles by viewModel.bookmarkedArticles.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val shareArticle = { article: NewsArticle ->
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this news article!")
            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\nRead more at: ${article.url}")
        }
        val chooser = Intent.createChooser(shareIntent, "Share News Article")
        context.startActivity(chooser)
    }

    LaunchedEffect(viewModel) {
        viewModel.errorEvent.collect { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                isDarkMode = isDarkMode,
                onRefresh = viewModel::refreshHeadlines,
                onThemeToggle = viewModel::toggleTheme,
                onSettingsClick = {
                    scope.launch { snackbarHostState.showSnackbar("Settings coming soon") }
                }
            )
        },
        bottomBar = {
            DeshNewsBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> { // Home
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "homeContentTransition",
                        modifier = Modifier.fillMaxSize()
                    ) { state: NewsUiState ->
                        when (state) {
                            is NewsUiState.Loading -> LoadingContent()
                            is NewsUiState.Success -> SuccessContent(
                                state = state,
                                onArticleClick = onArticleClick,
                                onBookmarkClick = { article ->
                                    viewModel.toggleBookmark(article.url)
                                    scope.launch {
                                        val message = if (article.isBookmarked) "Removed from Saved" else "Article Saved"
                                        snackbarHostState.showSnackbar(message)
                                    }
                                },
                                onShareClick = shareArticle
                            )
                            is NewsUiState.Error -> ErrorContent(
                                state = state,
                                onArticleClick = onArticleClick,
                                onRetry = viewModel::refreshHeadlines,
                                onShareClick = shareArticle
                            )
                        }
                    }
                }
                1 -> { // Categories
                    CategoriesContent { category ->
                        viewModel.setCategory(category.lowercase())
                        selectedTab = 0
                    }
                }
                2 -> { // Saved
                    SavedContent(
                        articles = savedArticles,
                        onArticleClick = onArticleClick,
                        onBookmarkClick = { article ->
                            viewModel.toggleBookmark(article.url)
                            scope.launch {
                                snackbarHostState.showSnackbar("Removed from Saved")
                            }
                        },
                        onShareClick = shareArticle
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    isDarkMode: Boolean,
    onRefresh: () -> Unit,
    onThemeToggle: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        BroadcastRed.copy(alpha = if (isDarkMode) 0.15f else 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            BreakingNewsBadge(fontSize = 12.sp)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoriesContent(onCategoryClick: (String) -> Unit) {
    val categories = listOf("World", "Politics", "Business", "Technology", "Science", "Health", "Sports", "Entertainment")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(title = "EXPLORE CATEGORIES")
            Spacer(Modifier.height(12.dp))
        }
        items(categories) { category ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onCategoryClick(category) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = BroadcastRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedContent(
    articles: List<NewsArticle>,
    onArticleClick: (String) -> Unit,
    onBookmarkClick: (NewsArticle) -> Unit,
    onShareClick: (NewsArticle) -> Unit
) {
    if (articles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No saved articles yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(title = "BOOKMARKED NEWS")
            Spacer(Modifier.height(12.dp))
        }
        items(articles, key = { it.url }) { article ->
            HeadlineCard(
                article = article,
                onClick = { onArticleClick(article.url) },
                onBookmarkClick = { onBookmarkClick(article) },
                onShareClick = { onShareClick(article) }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color       = BroadcastRed,
                strokeWidth = 3.dp,
                modifier    = Modifier.size(48.dp)
            )
            Text(
                text  = "Loading breaking news…",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: NewsUiState.Success,
    onArticleClick: (String) -> Unit,
    onBookmarkClick: (NewsArticle) -> Unit,
    onShareClick: (NewsArticle) -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            StudioBannerCarousel(
                articles       = state.featuredArticles,
                onArticleClick = { onArticleClick(it.url) }
            )
            Spacer(Modifier.height(20.dp))
        }

        item {
            SectionHeader(
                title = "TOP HEADLINES",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        items(
            items = state.headlines,
            key   = { it.url }
        ) { article ->
            HeadlineCard(
                article  = article,
                onClick  = { onArticleClick(article.url) },
                onBookmarkClick = { onBookmarkClick(article) },
                onShareClick = { onShareClick(article) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    state: NewsUiState.Error,
    onArticleClick: (String) -> Unit,
    onRetry: () -> Unit,
    onShareClick: (NewsArticle) -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BroadcastRed.copy(alpha = 0.15f))
                    .border(1.dp, BroadcastRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onRetry)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text      = "⚠ Network Error",
                        style     = MaterialTheme.typography.titleMedium.copy(
                            color = BroadcastRed
                        )
                    )
                    Text(
                        text      = state.message,
                        style     = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text      = "Tap to retry",
                        style     = MaterialTheme.typography.labelMedium.copy(
                            color = StudioGold
                        )
                    )
                }
            }
        }

        if (state.cachedArticles.isNotEmpty()) {
            item {
                SectionHeader(
                    title    = "CACHED NEWS",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
            items(
                items = state.cachedArticles,
                key   = { it.url }
            ) { article ->
                HeadlineCard(
                    article  = article,
                    onClick  = { onArticleClick(article.url) },
                    onShareClick = { onShareClick(article) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onViewAll: (() -> Unit)? = null
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BroadcastRed)
            )
            Text(
                text       = title,
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 14.sp,
                letterSpacing = 1.sp
            )
        }
        if (onViewAll != null) {
            Text(
                text      = "View All  ›",
                color     = StudioGold,
                fontSize  = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier  = Modifier.clickable(onClick = onViewAll)
            )
        }
    }
}

@Composable
private fun DeshNewsBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Home",       Icons.Default.Home,            Icons.Default.Home),
        NavItem("Categories", Icons.Default.Category,         Icons.Default.Category),
        NavItem("Saved",      Icons.Outlined.BookmarkBorder,  Icons.Default.Bookmark)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier       = Modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = if (MaterialTheme.colorScheme.background == Color.White) 0.5f else 1f),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected  = selectedTab == index,
                onClick   = { onTabSelected(index) },
                icon      = {
                    Icon(
                        imageVector        = if (selectedTab == index) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier.size(22.dp)
                    )
                },
                label     = {
                    Text(
                        text     = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors    = NavigationBarItemDefaults.colors(
                    selectedIconColor   = BroadcastRed,
                    selectedTextColor   = BroadcastRed,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = BroadcastRed.copy(alpha = 0.12f)
                )
            )
        }
    }
}

private data class NavItem(val label: String, val icon: ImageVector, val selectedIcon: ImageVector)
