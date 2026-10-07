package com.april.blockstar.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    shineEventId: Long = 0L,
    modifier: Modifier = Modifier
) {
    val shinePosition = remember { Animatable(-0.8f) }
    LaunchedEffect(shineEventId) {
        if (shineEventId > 0L) {
            shinePosition.snapTo(-0.8f)
            shinePosition.animateTo(1.8f, animationSpec = tween(durationMillis = 280))
        }
    }

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
                colors = listOf(Color.White.copy(alpha = 0.29f), Color.Transparent),
                center = Offset(size.width * 0.42f, size.height * 0.38f),
                radius = size.minDimension * 0.5f
            ),
            topLeft = Offset(edge, edge),
            size = Size(size.width - edge * 2f, size.height - edge * 2f),
            cornerRadius = CornerRadius(radius * 0.45f, radius * 0.45f)
        )

        // A small permanent specular highlight makes every block read as polished glass.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.82f), Color.White.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(size.width * 0.27f, size.height * 0.24f),
                radius = size.minDimension * 0.24f
            ),
            radius = size.minDimension * 0.24f,
            center = Offset(size.width * 0.27f, size.height * 0.24f)
        )

        if (shineEventId > 0L) {
            val sweepX = size.width * shinePosition.value
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.92f), Color.White.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(sweepX, size.height * 0.38f),
                    radius = size.minDimension * 0.38f
                ),
                radius = size.minDimension * 0.38f,
                center = Offset(sweepX, size.height * 0.38f)
            )
        }
        drawRoundRect(
            color = Color.White.copy(alpha = 0.38f),
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2f, size.height - inset * 2f),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = size.minDimension * 0.028f)
        )
    }
}
