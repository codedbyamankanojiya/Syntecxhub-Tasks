@file:OptIn(ExperimentalFoundationApi::class)
package com.deshnews.app.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.presentation.ui.theme.BroadcastRed
import com.deshnews.app.presentation.ui.theme.CardBorder
import com.deshnews.app.presentation.ui.theme.CardSurface
import com.deshnews.app.presentation.ui.theme.DarkRed
import com.deshnews.app.presentation.ui.theme.ElevatedSurface
import com.deshnews.app.presentation.ui.theme.MutedText
import com.deshnews.app.presentation.ui.theme.PureWhite
import com.deshnews.app.presentation.ui.theme.StudioGold
import kotlinx.coroutines.delay

/**
 * Full-width Studio Banner Carousel featuring top stories in a 3D broadcast stage card.
 *
 * Card anatomy (horizontal split):
 * ┌───────────────────────┬──────────────────────┐
 * │  RED BREAKING PLATE   │   HIGH-RES PHOTO     │
 * │  [BREAKING][NEWS]     │                      │
 * │                       │                      │
 * │  Bold white headline  │   (AsyncImage)       │
 * │                       │                      │
 * │  Source name          │                      │
 * └───────────────────────┴──────────────────────┘
 *       ◉  ○  ○  ○   ← animated pager dots
 *
 * Auto-scrolls every 4 seconds.
 *
 * @param articles       List of featured articles to display.
 * @param onArticleClick Called with the tapped article.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudioBannerCarousel(
    articles: List<NewsArticle>,
    onArticleClick: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    if (articles.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { articles.size })

    // Auto-advance every 4 seconds
    LaunchedEffect(articles.size) {
        while (true) {
            delay(4_000L)
            val next = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalPager(
            state          = pagerState,
            modifier       = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing    = 12.dp
        ) { page ->
            StudioBannerCard(
                article = articles[page],
                onClick = { onArticleClick(articles[page]) }
            )
        }

        // Dot indicators
        PagerDotIndicators(
            count      = articles.size,
            pagerState = pagerState,
            modifier   = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ─── Internal: Studio Card ──────────────────────────────────────────────────

@Composable
private fun StudioBannerCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(BroadcastRed, CardBorder))
                ),
                shape  = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            // ── LEFT PANE — Red Breaking Plate ────────────────────
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BroadcastRed, DarkRed)
                        )
                    )
                    .padding(14.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BreakingNewsBadge(fontSize = 9.sp)

                    Text(
                        text     = article.title,
                        color    = PureWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 16.sp,
                        lineHeight = 21.sp,
                        maxLines   = 5,
                        overflow   = TextOverflow.Ellipsis
                    )

                    Text(
                        text     = article.sourceName,
                        color    = PureWhite.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── RIGHT PANE — Featured Photo ───────────────────────
            Box(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
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
                            .background(ElevatedSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📰", fontSize = 44.sp)
                    }
                }

                // Gradient blending the two panes at the seam
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(28.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                listOf(DarkRed, Color.Transparent)
                            )
                        )
                )
            }
        }
    }
}

// ─── Internal: Pager Dot Indicators ─────────────────────────────────────────

@Composable
private fun PagerDotIndicators(
    count: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier               = modifier,
        horizontalArrangement  = Arrangement.spacedBy(6.dp),
        verticalAlignment      = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val isSelected = pagerState.currentPage == index
            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 18.dp else 6.dp,
                label       = "dotWidth_$index"
            )
            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) StudioGold
                        else MutedText.copy(alpha = 0.35f)
                    )
            )
        }
    }
}
