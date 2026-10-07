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
        val colors = BlockColor.entries.shuffled(random)
        var hasLongBar = false
        return List(3) { index ->
            val shape = randomGameShape(allowLongBar = !hasLongBar)
            if (shape.isLongBar()) hasLongBar = true
            PendingBlock(
                shape = shape,
                color = colors[index]
            )
        }
    }

    fun randomBlock(shapePool: List<BlockShape>): PendingBlock {
        val pool = shapePool.ifEmpty { allShapes }
        return PendingBlock(
            shape = pool.random(random),
            color = BlockColor.entries.random(random)
        )
    }

    fun randomBlocks(shapePool: List<BlockShape>, count: Int): List<PendingBlock> {
        val pool = shapePool.ifEmpty { allShapes }
        val colors = BlockColor.entries.shuffled(random)
        return List(count) { index ->
            PendingBlock(
                shape = pool.random(random),
                color = colors[index % colors.size]
            )
        }
    }

    private fun randomGameShape(allowLongBar: Boolean): BlockShape {
        val families = if (allowLongBar) {
            weightedFamilies
        } else {
            weightedFamilies.filterNot { family -> family.any { shape -> shape.isLongBar() } }
        }
        return families.random(random).random(random)
    }

    private fun BlockShape.isLongBar(): Boolean {
        return cellCount >= 4 && (width == 1 || height == 1)
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
        private val smallLFamily = rotations(
            idPrefix = "small_l",
            name = "3 格 L",
            cells = listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1))
        )
        val smallL: BlockShape = smallLFamily.first()

        val simpleShapes: List<BlockShape> = listOf(
            single,
            horizontal2,
            vertical2,
            horizontal3,
            vertical3,
            square2,
            smallL
        )

        val addToolShapes: List<BlockShape> = listOf(single)

        private val horizontal4 = BlockShape("horizontal_4", "4 横", line(4, true))
        private val vertical4 = BlockShape("vertical_4", "4 竖", line(4, false))
        private val horizontal5 = BlockShape("horizontal_5", "5 横", line(5, true))
        private val vertical5 = BlockShape("vertical_5", "5 竖", line(5, false))
        private val square3 = BlockShape("square_3", "3×3", rect(3, 3))
        private val rectangle2x3 = BlockShape("rect_2x3", "2×3", rect(2, 3))
        private val rectangle3x2 = BlockShape("rect_3x2", "3×2", rect(3, 2))

        private val fourLFamily = rotations(
            "l_4",
            "4 格 L",
            listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1))
        )
        private val fiveLFamily = rotations(
            "l_5",
            "5 格 L",
            listOf(
                CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2),
                CellOffset(1, 0), CellOffset(2, 0)
            )
        )
        private val uFamily = rotations(
            "u_5",
            "凹",
            listOf(
                CellOffset(0, 0), CellOffset(0, 2),
                CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2)
            )
        )

        private val shapeFamilies: List<List<BlockShape>> = listOf(
            listOf(single),
            listOf(horizontal2, vertical2),
            listOf(horizontal3, vertical3),
            listOf(horizontal4, vertical4),
            listOf(horizontal5, vertical5),
            listOf(square2),
            listOf(square3),
            listOf(rectangle2x3, rectangle3x2),
            smallLFamily,
            fourLFamily,
            fiveLFamily,
            uFamily
        )

        // Normal families have weight 3. The U family has weight 1, so it stays noticeably rarer.
        val weightedFamilies: List<List<BlockShape>> = buildList {
            shapeFamilies.dropLast(1).forEach { family ->
                add(family)
                add(family)
                add(family)
            }
            add(uFamily)
        }

        val allShapes: List<BlockShape> = shapeFamilies.flatten()

        fun shapeById(id: String): BlockShape? {
            return allShapes.firstOrNull { it.id == id }
        }

        private fun rotations(
            idPrefix: String,
            name: String,
            cells: List<CellOffset>
        ): List<BlockShape> {
            val results = mutableListOf<List<CellOffset>>()
            var current = normalize(cells)
            repeat(4) {
                if (results.none { it == current }) results += current
                current = normalize(current.map { CellOffset(it.col, -it.row) })
            }
            return results.mapIndexed { index, offsets ->
                BlockShape(
                    id = if (index == 0) idPrefix else "${idPrefix}_r$index",
                    name = name,
                    cells = offsets
                )
            }
        }

        private fun normalize(cells: List<CellOffset>): List<CellOffset> {
            val minRow = cells.minOf { it.row }
            val minCol = cells.minOf { it.col }
            return cells
                .map { CellOffset(it.row - minRow, it.col - minCol) }
                .sortedWith(compareBy<CellOffset> { it.row }.thenBy { it.col })
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
