package com.deshnews.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.deshnews.app.domain.model.NewsArticle
import com.deshnews.app.presentation.ui.theme.StudioGold

/**
 * Compact headline card composable.
 */
@Composable
fun HeadlineCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBookmarkClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width  = 1.dp,
                color  = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape  = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Left: Badge + Headline + Source ───────────────────────
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BreakingNewsBadge(fontSize = 9.sp)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onShareClick != null) {
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (onBookmarkClick != null) {
                        IconButton(
                            onClick = onBookmarkClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (article.isBookmarked) 
                                    Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (article.isBookmarked) StudioGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text     = article.title,
                style    = MaterialTheme.typography.titleMedium.copy(
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontSize   = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )

            Text(
                text  = article.sourceName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(Modifier.width(12.dp))

        // ── Right: Thumbnail ───────────────────────────────────────
        if (article.imageUrl.isNotBlank()) {
            AsyncImage(
                model              = article.imageUrl,
                contentDescription = article.title,
                modifier           = Modifier
                    .size(width = 96.dp, height = 76.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier          = Modifier
                    .size(width = 96.dp, height = 76.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment  = Alignment.Center
            ) {
                Text(text = "📰", fontSize = 26.sp)
            }
        }
    }
}

/**
 * Small related-news card used in the horizontal rail on the detail screen.
 */
@Composable
fun RelatedNewsCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp, 
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), 
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (article.imageUrl.isNotBlank()) {
            AsyncImage(
                model              = article.imageUrl,
                contentDescription = article.title,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentScale       = ContentScale.Crop
            )
        } else {
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("📰", fontSize = 28.sp)
            }
        }

        Column(
            modifier            = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BreakingNewsBadge(fontSize = 8.sp)

            Text(
                text     = article.title,
                style    = MaterialTheme.typography.bodySmall.copy(
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontSize   = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )
        }
    }
}
