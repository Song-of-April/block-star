package com.april.blockstar.presentation.game

import com.april.blockstar.domain.config.BOARD_SIZE
import kotlin.math.roundToInt

data class BoardTarget(
    val row: Int,
    val col: Int
)

fun resolveDragTarget(
    anchorX: Float,
    anchorY: Float,
    boardLeft: Float,
    boardTop: Float,
    cellSize: Float,
    shapeWidth: Int,
    shapeHeight: Int,
    boardSize: Int = BOARD_SIZE
): BoardTarget? {
    if (cellSize <= 0f) return null

    val boardRight = boardLeft + cellSize * boardSize
    val boardBottom = boardTop + cellSize * boardSize
    if (anchorX < boardLeft || anchorX >= boardRight || anchorY < boardTop || anchorY >= boardBottom) {
        return null
    }

    val col = ((anchorX - boardLeft) / cellSize - shapeWidth / 2f).roundToInt()
    val row = ((anchorY - boardTop) / cellSize - shapeHeight / 2f).roundToInt()
    return BoardTarget(row = row, col = col)
}
