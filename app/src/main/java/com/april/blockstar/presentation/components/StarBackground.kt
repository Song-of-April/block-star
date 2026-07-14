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

private val stars = listOf(
    Offset(0.12f, 0.14f),
    Offset(0.31f, 0.22f),
    Offset(0.76f, 0.18f),
    Offset(0.88f, 0.34f),
    Offset(0.18f, 0.64f),
    Offset(0.72f, 0.72f),
    Offset(0.42f, 0.86f)
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
                        Color(0xFF04C9E4),
                        Color(0xFF0A87B9),
                        Color(0xFF064261),
                        Color(0xFF041B3C)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEachIndexed { index, star ->
                val alpha = if (index % 2 == 0) pulse else 1f - pulse * 0.45f
                drawCircle(
                    color = Color.White.copy(alpha = alpha.coerceIn(0.25f, 0.9f)),
                    radius = if (index % 3 == 0) 2.6f else 1.8f,
                    center = Offset(size.width * star.x, size.height * star.y)
                )
            }
        }
        content()
    }
}
