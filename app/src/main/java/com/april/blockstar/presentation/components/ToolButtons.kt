package com.april.blockstar.presentation.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ToolButtons(
    onRefreshClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddClick: () -> Unit,
    refreshUsesLeft: Int,
    deleteUsesLeft: Int,
    addUsesLeft: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(46.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundToolButton(
            text = "⟳",
            colors = listOf(Color(0xFFA6F7FF), Color(0xFF19B7EF), Color(0xFF087BD3)),
            usesLeft = refreshUsesLeft,
            onClick = onRefreshClick
        )
        RoundToolButton(
            text = "◆",
            colors = listOf(Color(0xFFFFFFA3), Color(0xFFFFB622), Color(0xFFFF7214)),
            usesLeft = deleteUsesLeft,
            onClick = onDeleteClick
        )
        RoundToolButton(
            text = "+",
            colors = listOf(Color(0xFFFFC1E4), Color(0xFFFF5BAA), Color(0xFFD91B75)),
            usesLeft = addUsesLeft,
            onClick = onAddClick
        )
    }
}

@Composable
private fun RoundToolButton(
    text: String,
    colors: List<Color>,
    usesLeft: Int,
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
            .size(58.dp)
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
            .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(21.dp)
                .clip(CircleShape)
                .background(Color(0xDD062F4A))
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = usesLeft.coerceAtLeast(0).toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
