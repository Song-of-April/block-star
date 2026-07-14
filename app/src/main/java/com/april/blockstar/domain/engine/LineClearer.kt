package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.model.Board

data class LineClearResult(
    val board: Board,
    val clearedLineCount: Int
)

data class FullLines(
    val rows: List<Int>,
    val columns: List<Int>
) {
    val count: Int
        get() = rows.size + columns.size
}

object LineClearer {
    fun findFullLines(board: Board): FullLines {
        val fullRows = (0 until BOARD_SIZE).filter { row ->
            (0 until BOARD_SIZE).all { col -> board.cellAt(row, col).isOccupied }
        }
        val fullCols = (0 until BOARD_SIZE).filter { col ->
            (0 until BOARD_SIZE).all { row -> board.cellAt(row, col).isOccupied }
        }
        return FullLines(rows = fullRows, columns = fullCols)
    }

    fun clearFullLines(board: Board): LineClearResult {
        val fullLines = findFullLines(board)

        if (fullLines.count == 0) {
            return LineClearResult(board = board, clearedLineCount = 0)
        }

        val cellsToClear = buildSet {
            fullLines.rows.forEach { row ->
                (0 until BOARD_SIZE).forEach { col -> add(row to col) }
            }
            fullLines.columns.forEach { col ->
                (0 until BOARD_SIZE).forEach { row -> add(row to col) }
            }
        }

        return LineClearResult(
            board = board.clearCells(cellsToClear),
            clearedLineCount = fullLines.count
        )
    }
}
