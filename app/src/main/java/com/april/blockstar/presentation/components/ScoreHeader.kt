package com.april.blockstar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScoreHeader(
    score: Int,
    highestScore: Int,
    coins: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(105.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(50.dp)
                .shadow(8.dp, CircleShape, ambientColor = Color.White, spotColor = Color(0xFF9B7BFF))
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFE5C9FF), Color(0xFF9F5BE9), Color(0xFF5C2EBA))))
                .clickable(onClick = onPauseClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ⅱ",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = score.toString(),
                    modifier = Modifier.offset(y = 3.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color(0xFFD56A00),
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color(0xFFFFD34D),
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "最高分数：$highestScore",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-2).dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .shadow(5.dp, CircleShape, ambientColor = Color(0xFFFFE34F), spotColor = Color(0xFFFFA800))
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFFFFFF9B), Color(0xFFFFC400), Color(0xFFFF8A00)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    color = Color(0xFFFF8A00),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = coins.toString(),
                color = Color(0xFFFFD94D),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}
