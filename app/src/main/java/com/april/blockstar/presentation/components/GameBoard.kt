package com.april.blockstar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.model.Board
import com.april.blockstar.presentation.game.PlacementPreview
import com.april.blockstar.presentation.game.ToolMode

@Composable
fun GameBoard(
    board: Board,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier,
    placementPreview: PlacementPreview? = null,
    toolMode: ToolMode = ToolMode.None,
    dimmed: Boolean = false,
    onPositioned: (LayoutCoordinates) -> Unit = {}
) {
    val previewCells = placementPreview?.block?.shape?.cells
        ?.associate { offset -> (placementPreview.row + offset.row to placementPreview.col + offset.col) to placementPreview }
        .orEmpty()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF55F2FF))
            .border(2.dp, Color(0xFF9DFFFF), RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp))
                .background(if (dimmed) Color(0xAA031C2D) else Color(0xFF052F47))
                .onGloballyPositioned(onPositioned),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            repeat(BOARD_SIZE) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    repeat(BOARD_SIZE) { col ->
                        val color = board.colorAt(row, col)
                        val preview = previewCells[row to col]
                        val canDelete = toolMode == ToolMode.Delete && color != null
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(Color(0xFF0A425E))
                                .border(
                                    width = if (canDelete) 2.dp else 0.5.dp,
                                    color = if (canDelete) Color(0xFFFFD76A) else Color(0x553EDCEB)
                                )
                                .clickable { onCellClick(row, col) }
                                .padding(1.dp)
                        ) {
                            if (color != null) {
                                GemCell(
                                    color = color.toUiColor(),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (preview != null) {
                                val previewColor = if (preview.isValid) {
                                    preview.block.color.toUiColor().copy(alpha = 0.55f)
                                } else {
                                    Color(0xFFFF2F45).copy(alpha = 0.65f)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(previewColor)
                                        .border(1.dp, Color.White.copy(alpha = 0.65f), RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
