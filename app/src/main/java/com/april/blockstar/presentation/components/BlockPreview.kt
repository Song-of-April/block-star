package com.april.blockstar.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.april.blockstar.domain.model.PendingBlock

@Composable
fun BlockPreview(
    block: PendingBlock,
    modifier: Modifier = Modifier,
    cellColor: Color = block.color.toUiColor(),
    normalizedGridSize: Int? = null
) {
    BoxWithConstraints(modifier = modifier) {
        val cellSize = if (normalizedGridSize == null) {
            minOf(
                maxWidth / block.shape.width.toFloat(),
                maxHeight / block.shape.height.toFloat()
            )
        } else {
            minOf(
                maxWidth / normalizedGridSize.toFloat(),
                maxHeight / normalizedGridSize.toFloat()
            )
        }
        val startX = (maxWidth - cellSize * block.shape.width.toFloat()) / 2f
        val startY = (maxHeight - cellSize * block.shape.height.toFloat()) / 2f

        block.shape.cells.forEach { cell ->
            Box(
                modifier = Modifier
                    .offset(
                        x = startX + cellSize * cell.col.toFloat(),
                        y = startY + cellSize * cell.row.toFloat()
                    )
                    .size(cellSize)
            ) {
                GemCell(
                    color = cellColor,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}
