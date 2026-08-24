package com.deshnews.app.presentation.ui.screen

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.presentation.state.DetailUiState
import com.deshnews.app.presentation.ui.components.BreakingNewsBadge
import com.deshnews.app.presentation.ui.components.RelatedNewsCard
import com.deshnews.app.presentation.ui.theme.BroadcastRed
import com.deshnews.app.presentation.ui.theme.LiveGreen
import com.deshnews.app.presentation.ui.theme.OverlayDark
import com.deshnews.app.presentation.ui.theme.PureWhite
import com.deshnews.app.presentation.ui.theme.StudioGold
import com.deshnews.app.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun NewsDetailScreen(
    articleUrl: String,
    onBack: () -> Unit,
    onArticleClick: (String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailUiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(articleUrl) {
        viewModel.loadArticleDetail(articleUrl)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = detailState) {
                is DetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color       = BroadcastRed,
                            strokeWidth = 3.dp,
                            modifier    = Modifier.size(48.dp)
                        )
                    }
                }

                is DetailUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text("⚠", fontSize = 48.sp)
                            Text(
                                text  = state.message,
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint               = BroadcastRed
                                )
                            }
                        }
                    }
                }

                is DetailUiState.Success -> {
                    DetailContent(
                        article        = state.article,
                        relatedArticles = state.relatedArticles,
                        onBack         = onBack,
                        onArticleClick = onArticleClick,
                        onBookmark     = {
                            viewModel.toggleBookmark(state.article.url)
                            scope.launch {
                                val message = if (state.article.isBookmarked) "Removed from Saved" else "Article Saved"
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        onShare        = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Check out this news article!")
                                putExtra(Intent.EXTRA_TEXT, "${state.article.title}\n\nRead more at: ${state.article.url}")
                            }
                            val chooser = Intent.createChooser(shareIntent, "Share News Article")
                            context.startActivity(chooser)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    article: NewsArticle,
    relatedArticles: List<NewsArticle>,
    onBack: () -> Unit,
    onArticleClick: (String) -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            HeroImageSection(
                article    = article,
                onBack     = onBack,
                onBookmark = onBookmark,
                onShare    = onShare
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            TimestampChipsRow(publishedAt = article.publishedAt)
            Spacer(Modifier.height(12.dp))
        }

        item {
            Text(
                text     = article.title,
                style    = MaterialTheme.typography.displaySmall.copy(
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BroadcastRed)
                )
                Text(
                    text  = article.sourceName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BroadcastRed
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            ArticleBody(
                content  = article.content.ifBlank { article.description },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        if (relatedArticles.isNotEmpty()) {
            item {
                RelatedNewsSection(
                    articles        = relatedArticles,
                    onArticleClick  = { article -> onArticleClick(article.url) }
                )
            }
        }
    }
}

@Composable
private fun HeroImageSection(
    article: NewsArticle,
    onBack: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        if (article.imageUrl.isNotBlank()) {
            AsyncImage(
                model              = article.imageUrl,
                contentDescription = article.title,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("📰", fontSize = 64.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OverlayDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = PureWhite,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            Row {
                IconButton(onClick = onBookmark) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OverlayDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (article.isBookmarked)
                                Icons.Filled.Bookmark
                            else
                                Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint               = if (article.isBookmarked) StudioGold else PureWhite,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(onClick = onShare) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OverlayDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Share,
                            contentDescription = "Share",
                            tint               = PureWhite,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        BreakingNewsBadge(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp),
            cornerRadius = 6.dp,
            fontSize     = 13.sp
        )
    }
}

@Composable
private fun TimestampChipsRow(publishedAt: String) {
    val (dateStr, timeStr) = formatPublishedAt(publishedAt)

    Row(
        modifier              = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        TimestampChip(icon = "📅", text = dateStr)
        TimestampChip(icon = "🕐", text = timeStr)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(LiveGreen.copy(alpha = 0.15f))
                .border(1.dp, LiveGreen.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LiveGreen)
                )
                Text(
                    text       = "LIVE",
                    color      = LiveGreen,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun TimestampChip(icon: String, text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                text     = text,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ArticleBody(content: String, modifier: Modifier = Modifier) {
    val paragraphs = content
        .split("\n\n", "\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .ifEmpty { listOf(content) }

    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        paragraphs.forEach { paragraph ->
            Text(
                text  = paragraph,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 26.sp
                )
            )
        }
    }
}

@Composable
private fun RelatedNewsSection(
    articles: List<NewsArticle>,
    onArticleClick: (NewsArticle) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
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
                text       = "RELATED NEWS",
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Row(
            modifier              = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            articles.forEach { article ->
                RelatedNewsCard(
                    article  = article,
                    onClick  = { onArticleClick(article) }
                )
            }
            Spacer(Modifier.width(0.dp))
        }
    }
}

private fun formatPublishedAt(raw: String): Pair<String, String> {
    return try {
        val datePart = raw.substringBefore("T")
        val timePart = raw.substringAfter("T").take(5)

        val (year, month, day) = datePart.split("-").map { it.toInt() }
        val monthNames = listOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val (hour, minute) = timePart.split(":").map { it.toInt() }
        val amPm  = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0   -> 12
            hour > 12   -> hour - 12
            else        -> hour
        }

        val dateFormatted = "$day ${monthNames.getOrElse(month) { "?" }} $year"
        val timeFormatted = "%02d:%02d %s".format(hour12, minute, amPm)

        dateFormatted to timeFormatted
    } catch (e: Exception) {
        "—" to "—"
    }
}
