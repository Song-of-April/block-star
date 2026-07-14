package com.april.blockstar.domain.engine

import com.april.blockstar.domain.model.BlockColor
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.CellOffset
import com.april.blockstar.domain.model.PendingBlock
import kotlin.random.Random

class ShapeGenerator(
    private val random: Random = Random.Default
) {
    fun generateRound(): List<PendingBlock?> {
        return List(3) {
            randomBlock(allShapes)
        }
    }

    fun randomBlock(shapePool: List<BlockShape>): PendingBlock {
        val pool = shapePool.ifEmpty { allShapes }
        return PendingBlock(
            shape = pool.random(random),
            color = BlockColor.entries.random(random)
        )
    }

    fun placeableShapes(board: Board, source: List<BlockShape> = allShapes): List<BlockShape> {
        return source.filter { shape -> hasAnyPlacement(board, shape) }
    }

    fun generatePlaceableBlocks(
        board: Board,
        count: Int,
        preferSimpleShapes: Boolean
    ): List<PendingBlock> {
        val simpleCandidates = placeableShapes(board, simpleShapes)
        val fallbackCandidates = placeableShapes(board, allShapes)
        val pool = if (preferSimpleShapes && simpleCandidates.isNotEmpty()) {
            simpleCandidates
        } else {
            fallbackCandidates
        }
        if (pool.isEmpty()) return emptyList()
        return List(count) { randomBlock(pool) }
    }

    private fun hasAnyPlacement(board: Board, shape: BlockShape): Boolean {
        return (0 until com.april.blockstar.domain.config.BOARD_SIZE).any { row ->
            (0 until com.april.blockstar.domain.config.BOARD_SIZE).any { col ->
                PlacementValidator.canPlace(board, shape, row, col)
            }
        }
    }

    companion object {
        val single: BlockShape = BlockShape("single", "1", listOf(CellOffset(0, 0)))
        val horizontal2: BlockShape = BlockShape("horizontal_2", "2 横", line(length = 2, horizontal = true))
        val vertical2: BlockShape = BlockShape("vertical_2", "2 竖", line(length = 2, horizontal = false))
        val horizontal3: BlockShape = BlockShape("horizontal_3", "3 横", line(length = 3, horizontal = true))
        val vertical3: BlockShape = BlockShape("vertical_3", "3 竖", line(length = 3, horizontal = false))
        val square2: BlockShape = BlockShape(
            "square_2",
            "2x2",
            listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1))
        )
        val smallL: BlockShape = BlockShape(
            "small_l",
            "小 L",
            listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1))
        )

        val simpleShapes: List<BlockShape> = listOf(
            single,
            horizontal2,
            vertical2,
            horizontal3,
            vertical3,
            square2,
            smallL,
            BlockShape(
                "small_l_mirror",
                "小 L",
                listOf(CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1))
            )
        )

        val addToolShapes: List<BlockShape> = listOf(
            single,
            horizontal2,
            vertical2,
            horizontal3,
            vertical3,
            square2,
            smallL
        )

        val allShapes: List<BlockShape> = simpleShapes + listOf(
            BlockShape("horizontal_4", "4 横", line(length = 4, horizontal = true)),
            BlockShape("vertical_4", "4 竖", line(length = 4, horizontal = false)),
            BlockShape("horizontal_5", "5 横", line(length = 5, horizontal = true)),
            BlockShape("vertical_5", "5 竖", line(length = 5, horizontal = false)),
            BlockShape(
                "square_3",
                "3x3",
                rect(rows = 3, cols = 3)
            ),
            BlockShape(
                "rect_2x3",
                "2x3",
                rect(rows = 2, cols = 3)
            ),
            BlockShape(
                "rect_3x2",
                "3x2",
                rect(rows = 3, cols = 2)
            ),
            BlockShape(
                "l_4_a",
                "L",
                listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1))
            ),
            BlockShape(
                "l_4_b",
                "L",
                listOf(CellOffset(0, 1), CellOffset(1, 1), CellOffset(2, 0), CellOffset(2, 1))
            ),
            BlockShape(
                "l_4_c",
                "L",
                listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0), CellOffset(2, 0))
            ),
            BlockShape(
                "l_4_d",
                "L",
                listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1), CellOffset(2, 1))
            ),
            BlockShape(
                "l_5",
                "大 L",
                listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1), CellOffset(2, 2))
            ),
            BlockShape(
                "t_4",
                "T",
                listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 1))
            ),
            BlockShape(
                "t_5",
                "T",
                listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 1), CellOffset(2, 1))
            ),
            BlockShape(
                "cross_5",
                "十字",
                listOf(CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2), CellOffset(2, 1))
            ),
            BlockShape(
                "corner_3",
                "角",
                listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1))
            ),
            BlockShape(
                "stair_4",
                "阶梯",
                listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1), CellOffset(1, 2))
            )
        )

        fun shapeById(id: String): BlockShape? {
            return allShapes.firstOrNull { it.id == id }
        }

        private fun line(length: Int, horizontal: Boolean): List<CellOffset> {
            return List(length) { index ->
                if (horizontal) CellOffset(0, index) else CellOffset(index, 0)
            }
        }

        private fun rect(rows: Int, cols: Int): List<CellOffset> {
            return buildList {
                repeat(rows) { row ->
                    repeat(cols) { col ->
                        add(CellOffset(row, col))
                    }
                }
            }
        }
    }
}
