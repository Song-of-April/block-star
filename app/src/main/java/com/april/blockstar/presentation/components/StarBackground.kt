package com.april.blockstar.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private data class Star(val position: Offset, val color: Color, val radius: Float)

private val stars = listOf(
    Star(Offset(0.10f, 0.13f), Color(0xFFFFFF3B), 2.2f),
    Star(Offset(0.34f, 0.25f), Color(0xFFFF8CE8), 2.5f),
    Star(Offset(0.71f, 0.10f), Color.White, 2.0f),
    Star(Offset(0.91f, 0.17f), Color(0xFF32FF55), 2.3f),
    Star(Offset(0.86f, 0.37f), Color(0xFFFF9AE4), 2.1f),
    Star(Offset(0.17f, 0.49f), Color.White, 1.8f),
    Star(Offset(0.76f, 0.56f), Color(0xFF45F4FF), 2.6f),
    Star(Offset(0.07f, 0.68f), Color(0xFFFFFF45), 2.0f),
    Star(Offset(0.31f, 0.74f), Color(0xFF36FF62), 2.2f),
    Star(Offset(0.63f, 0.80f), Color(0xFFFFA1E7), 2.4f),
    Star(Offset(0.84f, 0.88f), Color.White, 2.0f),
    Star(Offset(0.22f, 0.92f), Color(0xFF73F7FF), 1.8f)
)

@Composable
fun StarBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "stars")
    val pulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star-alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF08CDE2),
                        Color(0xFF05AFCB),
                        Color(0xFF075978),
                        Color(0xFF03324E),
                        Color(0xFF032A43)
                    ),
                    startY = 0f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEachIndexed { index, star ->
                val alpha = if (index % 2 == 0) pulse else 1f - pulse * 0.45f
                val center = Offset(size.width * star.position.x, size.height * star.position.y)
                drawCircle(
                    color = star.color.copy(alpha = alpha.coerceIn(0.28f, 0.92f)),
                    radius = star.radius,
                    center = center
                )
                if (index % 3 == 0) {
                    drawLine(
                        color = star.color.copy(alpha = alpha * 0.72f),
                        start = Offset(center.x - star.radius * 2.6f, center.y),
                        end = Offset(center.x + star.radius * 2.6f, center.y),
                        strokeWidth = 1.2f
                    )
                    drawLine(
                        color = star.color.copy(alpha = alpha * 0.72f),
                        start = Offset(center.x, center.y - star.radius * 2.6f),
                        end = Offset(center.x, center.y + star.radius * 2.6f),
                        strokeWidth = 1.2f
                    )
                }
            }
        }
        content()
    }
}
