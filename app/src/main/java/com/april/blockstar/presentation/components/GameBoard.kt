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
import androidx.compose.ui.draw.shadow
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
            .shadow(15.dp, RoundedCornerShape(7.dp), ambientColor = Color(0xFF4AFFFF), spotColor = Color(0xFF4AFFFF))
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFF50F7FF))
            .border(2.dp, Color(0xFFC4FFFF), RoundedCornerShape(7.dp))
            .padding(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
                .background(if (dimmed) Color(0xCC021D2C) else Color(0xFF02364C))
                .onGloballyPositioned(onPositioned),
            verticalArrangement = Arrangement.Top
        ) {
            repeat(BOARD_SIZE) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repeat(BOARD_SIZE) { col ->
                        val color = board.colorAt(row, col)
                        val preview = previewCells[row to col]
                        val canDelete = toolMode == ToolMode.Delete && color != null
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(Color(0xFF053B50))
                                .border(
                                    width = if (canDelete) 2.dp else 0.5.dp,
                                    color = if (canDelete) Color(0xFFFFDE62) else Color(0x8852C9D8)
                                )
                                .clickable { onCellClick(row, col) }
                                .padding(0.45.dp)
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
