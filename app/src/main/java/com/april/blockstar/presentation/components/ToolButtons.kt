package com.april.blockstar.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ToolButtons(
    onRefreshClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(46.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundToolButton(
            glyph = ToolGlyph.Refresh,
            colors = listOf(Color(0xFFA6F7FF), Color(0xFF19B7EF), Color(0xFF087BD3)),
            onClick = onRefreshClick
        )
        RoundToolButton(
            glyph = ToolGlyph.Hammer,
            colors = listOf(Color(0xFFFFFFA3), Color(0xFFFFB622), Color(0xFFFF7214)),
            onClick = onDeleteClick
        )
        RoundToolButton(
            glyph = ToolGlyph.Add,
            colors = listOf(Color(0xFFFFC1E4), Color(0xFFFF5BAA), Color(0xFFD91B75)),
            onClick = onAddClick
        )
    }
}

private enum class ToolGlyph {
    Refresh,
    Hammer,
    Add
}

@Composable
private fun RoundToolButton(
    glyph: ToolGlyph,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        label = "tool-button-scale"
    )

    Box(
        modifier = Modifier
            .size(62.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 10f
                shape = CircleShape
                clip = true
            }
            .shadow(9.dp, CircleShape, ambientColor = colors.first(), spotColor = colors.last())
            .clip(CircleShape)
            .background(Brush.radialGradient(colors))
            .border(3.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = 0.28f }
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height * 0.24f),
                    radius = size.minDimension * 0.42f
                )
            )
        }
        ToolGlyphIcon(
            glyph = glyph,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ToolGlyphIcon(
    glyph: ToolGlyph,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val white = Color.White
        val strokeWidth = size.minDimension * 0.12f
        when (glyph) {
            ToolGlyph.Refresh -> {
                drawArc(
                    color = white,
                    startAngle = -45f,
                    sweepAngle = 285f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                val arrow = Path().apply {
                    moveTo(size.width * 0.83f, size.height * 0.22f)
                    lineTo(size.width * 0.91f, size.height * 0.48f)
                    lineTo(size.width * 0.64f, size.height * 0.4f)
                    close()
                }
                drawPath(arrow, white)
            }

            ToolGlyph.Hammer -> rotate(degrees = -42f) {
                drawRoundRect(
                    color = white,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.18f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.27f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth * 0.55f)
                )
                drawRoundRect(
                    color = white,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.44f, size.height * 0.36f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.13f, size.height * 0.49f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth * 0.5f)
                )
            }

            ToolGlyph.Add -> {
                drawLine(
                    color = white,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.18f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.82f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = white,
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.5f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.5f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
