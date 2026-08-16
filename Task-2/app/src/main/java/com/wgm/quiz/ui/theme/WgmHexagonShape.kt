package com.wgm.quiz.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A custom shape that renders a horizontal stretched hexagon.
 * Perfect for the "Who's Gonna be Millionaire" question and option cards.
 */
class WgmHexagonShape(private val cornerOffsetRatio: Float = 0.15f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val offset = width * cornerOffsetRatio

            moveTo(offset, 0f)
            lineTo(width - offset, 0f)
            lineTo(width, height / 2f)
            lineTo(width - offset, height)
            lineTo(offset, height)
            lineTo(0f, height / 2f)
            close()
        }
        return Outline.Generic(path)
    }
}
