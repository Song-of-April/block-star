package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.ADD_TOOL_COST
import com.april.blockstar.domain.config.BOMB_TOOL_COST
import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.config.CLEAR_ONE_LINE_BONUS
import com.april.blockstar.domain.config.INITIAL_COINS
import com.april.blockstar.domain.config.REFRESH_TOOL_COST
import com.april.blockstar.domain.config.TOOL_USE_LIMIT
import com.april.blockstar.domain.model.BlockColor
import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.CellOffset
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.GameState
import com.april.blockstar.domain.model.PendingBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    @Test
    fun placingBlockClearsFullRowAndScoresOnce() {
        val engine = GameEngine()
        val board = boardWithOccupiedCells(
            (0 until BOARD_SIZE - 1).map { col -> 0 to col }
        )
        val block = PendingBlock(
            shape = BlockShape("single_test", "1", listOf(CellOffset(0, 0))),
            color = BlockColor.Red
        )
        val state = GameState(
            board = board,
            pendingBlocks = listOf(block, null, null),
            score = 0,
            highestScore = 0,
            coins = INITIAL_COINS,
            isGameOver = false
        )

        val result = requireNotNull(engine.placeBlock(state, 0, 0, BOARD_SIZE - 1))

        assertEquals(1, result.clearedLineCount)
        assertEquals(1 + CLEAR_ONE_LINE_BONUS, result.scoreAdded)
        assertFalse((0 until BOARD_SIZE).any { col -> result.state.board.cellAt(0, col).isOccupied })
    }

    @Test
    fun scoringUsesIncreasingBonusesForMultipleLines() {
        assertEquals(4, ScoreCalculator.scoreForPlacement(4, 0))
        assertEquals(19, ScoreCalculator.scoreForPlacement(4, 1))
        assertEquals(39, ScoreCalculator.scoreForPlacement(4, 2))
        assertEquals(79, ScoreCalculator.scoreForPlacement(4, 3))
        assertEquals(79, ScoreCalculator.scoreForPlacement(4, 5))
    }

    @Test
    fun lineClearerClearsCrossingRowAndColumnWithoutDoubleCountingCell() {
        val occupied = buildList {
            (0 until BOARD_SIZE).forEach { col -> add(0 to col) }
            (0 until BOARD_SIZE).forEach { row -> add(row to 0) }
        }
        val board = boardWithOccupiedCells(occupied)

        val result = LineClearer.clearFullLines(board)

        assertEquals(2, result.clearedLineCount)
        assertFalse(result.board.cellAt(0, 0).isOccupied)
        assertFalse(result.board.cellAt(0, 5).isOccupied)
        assertFalse(result.board.cellAt(5, 0).isOccupied)
    }

    @Test
    fun refreshKeepsEmptySlotsAndProducesPlaceableBlocks() {
        val engine = GameEngine()
        val state = engine.newGame().copy(
            pendingBlocks = listOf(
                PendingBlock(ShapeGenerator.horizontal3, BlockColor.Blue),
                null,
                PendingBlock(ShapeGenerator.vertical3, BlockColor.Green)
            ),
            coins = REFRESH_TOOL_COST
        )

        val result = requireNotNull(engine.refreshRemainingBlocks(state))

        assertEquals(0, result.state.coins)
        assertNotNull(result.state.pendingBlocks[0])
        assertEquals(null, result.state.pendingBlocks[1])
        assertNotNull(result.state.pendingBlocks[2])
        assertEquals(ShapeGenerator.single.id, result.state.pendingBlocks[0]?.shape?.id)
        assertEquals(ShapeGenerator.single.id, result.state.pendingBlocks[2]?.shape?.id)
        result.state.pendingBlocks.filterNotNull().forEachIndexed { index, _ ->
            val pendingIndex = if (index == 0) 0 else 2
            assertTrue(hasAnyPlacement(engine, result.state, pendingIndex))
        }
    }

    @Test
    fun addBlockUsesEmptySlotBeforeReplacing() {
        val engine = GameEngine()
        val state = engine.newGame().copy(
            pendingBlocks = listOf(null, PendingBlock(ShapeGenerator.horizontal2, BlockColor.Red), null),
            coins = ADD_TOOL_COST
        )

        val result = requireNotNull(engine.addBlock(state, ShapeGenerator.single))

        assertEquals(0, result.state.coins)
        assertNotNull(result.state.pendingBlocks[0])
        assertNotNull(result.state.pendingBlocks[1])
        assertEquals(null, result.state.pendingBlocks[2])
    }

    @Test
    fun addBlockReplacesRequestedSlotWhenAllSlotsAreFull() {
        val engine = GameEngine()
        val state = engine.newGame().copy(
            pendingBlocks = listOf(
                PendingBlock(ShapeGenerator.horizontal2, BlockColor.Red),
                PendingBlock(ShapeGenerator.vertical2, BlockColor.Blue),
                PendingBlock(ShapeGenerator.square2, BlockColor.Green)
            ),
            coins = ADD_TOOL_COST
        )

        val result = requireNotNull(
            engine.addBlock(state, ShapeGenerator.single, replaceIndex = 0)
        )

        assertEquals(0, result.state.coins)
        assertEquals(ShapeGenerator.single.id, result.state.pendingBlocks[0]?.shape?.id)
        assertEquals(ShapeGenerator.vertical2.id, result.state.pendingBlocks[1]?.shape?.id)
        assertEquals(ShapeGenerator.square2.id, result.state.pendingBlocks[2]?.shape?.id)
    }

    @Test
    fun addToolCandidatesOnlyOffersSingleCellBlock() {
        val engine = GameEngine()
        val state = engine.newGame().copy(coins = ADD_TOOL_COST)

        val candidates = engine.addToolCandidates(state)

        assertEquals(listOf(ShapeGenerator.single), candidates)
    }

    @Test
    fun toolCannotBeUsedMoreThanThreeTimesPerGame() {
        val engine = GameEngine()
        val state = engine.newGame().copy(
            coins = REFRESH_TOOL_COST,
            refreshUses = TOOL_USE_LIMIT
        )

        assertEquals(null, engine.refreshRemainingBlocks(state))
    }

    @Test
    fun bombRemovesOnlyAnUnplaceablePendingBlock() {
        val engine = GameEngine()
        val almostFullBoard = boardWithOccupiedCells(
            buildList {
                repeat(BOARD_SIZE) { row ->
                    repeat(BOARD_SIZE) { col ->
                        if (row != BOARD_SIZE - 1 || col != BOARD_SIZE - 1) add(row to col)
                    }
                }
            }
        )
        val state = engine.newGame().copy(
            board = almostFullBoard,
            pendingBlocks = listOf(
                PendingBlock(ShapeGenerator.square2, BlockColor.Red),
                PendingBlock(ShapeGenerator.single, BlockColor.Blue),
                null
            ),
            coins = BOMB_TOOL_COST
        )

        val result = requireNotNull(engine.bombPendingBlock(state, 0))

        assertEquals(0, result.state.coins)
        assertEquals(null, result.state.pendingBlocks[0])
        assertNotNull(result.state.pendingBlocks[1])
        assertEquals(null, engine.bombPendingBlock(state, 1))
    }

    @Test
    fun settlementAwardsOneCoinPerStartedFiftyPoints() {
        val engine = GameEngine()
        val result = engine.finishGame(engine.newGame().copy(score = 101))

        assertEquals(3, result.awardCoins)
    }

    private fun hasAnyPlacement(engine: GameEngine, state: GameState, pendingIndex: Int): Boolean {
        return (0 until BOARD_SIZE).any { row ->
            (0 until BOARD_SIZE).any { col ->
                engine.canPlace(state, pendingIndex, row, col)
            }
        }
    }

    private fun boardWithOccupiedCells(positions: List<Pair<Int, Int>>): Board {
        val colors = List(BOARD_SIZE) { row ->
            List(BOARD_SIZE) { col ->
                if ((row to col) in positions) BlockColor.Yellow else null
            }
        }
        return Board.fromColors(colors)
    }
}
