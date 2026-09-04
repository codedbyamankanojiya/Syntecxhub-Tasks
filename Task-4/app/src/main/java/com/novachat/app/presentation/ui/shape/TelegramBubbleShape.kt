package com.novachat.app.presentation.ui.shape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import com.novachat.app.presentation.ui.theme.NovaChatDimens

/**
 * Creates a simple rounded bubble shape with cluster-aware corners.
 * Consecutive messages from the same sender get a smaller corner
 * on the side closest to the adjacent message.
 */
fun chatBubbleShape(
    isOutgoing: Boolean,
    isFirstInCluster: Boolean,
    isLastInCluster: Boolean,
    cornerRadius: Dp = NovaChatDimens.BubbleCorner,
    smallCorner: Dp = NovaChatDimens.BubbleCornerSmall
): RoundedCornerShape {
    return if (isOutgoing) {
        RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = if (isFirstInCluster) cornerRadius else smallCorner,
            bottomEnd = if (isLastInCluster) cornerRadius else smallCorner,
            bottomStart = cornerRadius
        )
    } else {
        RoundedCornerShape(
            topStart = if (isFirstInCluster) cornerRadius else smallCorner,
            topEnd = cornerRadius,
            bottomEnd = cornerRadius,
            bottomStart = if (isLastInCluster) cornerRadius else smallCorner
        )
    }
}
