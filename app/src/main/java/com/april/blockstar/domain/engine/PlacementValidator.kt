package com.april.blockstar.domain.engine

import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.BlockShape

object PlacementValidator {
    fun canPlace(board: Board, shape: BlockShape, anchorRow: Int, anchorCol: Int): Boolean {
        return shape.cells.all { offset ->
            val row = anchorRow + offset.row
            val col = anchorCol + offset.col
            board.isInside(row, col) && !board.cellAt(row, col).isOccupied
        }
    }
}

