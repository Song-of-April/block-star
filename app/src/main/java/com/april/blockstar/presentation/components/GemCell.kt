package com.april.blockstar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun GemCell(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension * 0.07f
        val edge = size.minDimension * 0.18f
        val inset = size.minDimension * 0.035f

        // Dark outline separates adjacent gems just like the beveled source artwork.
        drawRoundRect(
            color = color.copy(alpha = 0.48f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(color.copy(alpha = 1f), color.copy(alpha = 0.82f)),
                start = Offset(inset, inset),
                end = Offset(size.width - inset, size.height - inset)
            ),
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2f, size.height - inset * 2f),
            cornerRadius = CornerRadius(radius, radius)
        )

        val topFacet = Path().apply {
            moveTo(inset, inset)
            lineTo(size.width - inset, inset)
            lineTo(size.width - edge, edge)
            lineTo(edge, edge)
            close()
        }
        drawPath(topFacet, Color.White.copy(alpha = 0.58f))

        val leftFacet = Path().apply {
            moveTo(inset, inset)
            lineTo(edge, edge)
            lineTo(edge, size.height - edge)
            lineTo(inset, size.height - inset)
            close()
        }
        drawPath(leftFacet, Color.White.copy(alpha = 0.18f))

        val rightFacet = Path().apply {
            moveTo(size.width - inset, inset)
            lineTo(size.width - edge, edge)
            lineTo(size.width - edge, size.height - edge)
            lineTo(size.width - inset, size.height - inset)
            close()
        }
        drawPath(rightFacet, Color.Black.copy(alpha = 0.28f))

        val bottomFacet = Path().apply {
            moveTo(inset, size.height - inset)
            lineTo(edge, size.height - edge)
            lineTo(size.width - edge, size.height - edge)
            lineTo(size.width - inset, size.height - inset)
            close()
        }
        drawPath(bottomFacet, Color.Black.copy(alpha = 0.32f))

        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                center = Offset(size.width * 0.42f, size.height * 0.38f),
                radius = size.minDimension * 0.5f
            ),
            topLeft = Offset(edge, edge),
            size = Size(size.width - edge * 2f, size.height - edge * 2f),
            cornerRadius = CornerRadius(radius * 0.45f, radius * 0.45f)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.38f),
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2f, size.height - inset * 2f),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = size.minDimension * 0.028f)
        )
    }
}
