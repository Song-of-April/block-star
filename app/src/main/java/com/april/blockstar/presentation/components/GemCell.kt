package com.april.blockstar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun GemCell(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension * 0.14f
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(color.copy(alpha = 1f), color.copy(alpha = 0.78f)),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.26f),
            topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
            size = Size(size.width * 0.84f, size.height * 0.16f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.12f),
            topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
            size = Size(size.width * 0.16f, size.height * 0.84f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(0f, size.height * 0.76f),
            size = Size(size.width, size.height * 0.24f),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.14f),
            topLeft = Offset(size.width * 0.78f, 0f),
            size = Size(size.width * 0.22f, size.height),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = Offset(size.width * 0.22f, size.height * 0.22f),
            size = Size(size.width * 0.56f, size.height * 0.56f),
            cornerRadius = CornerRadius(radius * 0.6f, radius * 0.6f),
            style = Stroke(width = size.minDimension * 0.04f)
        )
    }
}
