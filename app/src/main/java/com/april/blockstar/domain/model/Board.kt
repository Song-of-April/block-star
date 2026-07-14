package com.april.blockstar.domain.model

import com.april.blockstar.domain.config.BOARD_SIZE

data class Board(
    val cells: List<List<BoardCell>>
) {
    fun isInside(row: Int, col: Int): Boolean {
        return row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE
    }

    fun cellAt(row: Int, col: Int): BoardCell {
        require(isInside(row, col)) { "Cell is outside the board: $row, $col" }
        return cells[row][col]
    }

    fun colorAt(row: Int, col: Int): BlockColor? {
        return cellAt(row, col).color
    }

    fun place(shape: BlockShape, color: BlockColor, anchorRow: Int, anchorCol: Int): Board {
        val nextCells = cells.map { row -> row.toMutableList() }.toMutableList()
        shape.cells.forEach { offset ->
            val row = anchorRow + offset.row
            val col = anchorCol + offset.col
            nextCells[row][col] = BoardCell(color)
        }
        return Board(nextCells.map { it.toList() })
    }

    fun removeCell(row: Int, col: Int): Board {
        if (!isInside(row, col)) return this
        val nextCells = cells.map { boardRow -> boardRow.toMutableList() }.toMutableList()
        nextCells[row][col] = BoardCell()
        return Board(nextCells.map { it.toList() })
    }

    fun clearCells(positions: Set<Pair<Int, Int>>): Board {
        val nextCells = cells.map { row -> row.toMutableList() }.toMutableList()
        positions.forEach { (row, col) ->
            nextCells[row][col] = BoardCell()
        }
        return Board(nextCells.map { it.toList() })
    }

    fun hasOccupiedCells(): Boolean {
        return cells.any { row -> row.any { it.isOccupied } }
    }

    companion object {
        fun empty(): Board {
            return Board(
                List(BOARD_SIZE) {
                    List(BOARD_SIZE) { BoardCell() }
                }
            )
        }

        fun fromColors(colors: List<List<BlockColor?>>): Board {
            require(colors.size == BOARD_SIZE) { "Board must have $BOARD_SIZE rows" }
            require(colors.all { it.size == BOARD_SIZE }) { "Board must have $BOARD_SIZE columns" }
            return Board(colors.map { row -> row.map { BoardCell(it) } })
        }
    }
}
