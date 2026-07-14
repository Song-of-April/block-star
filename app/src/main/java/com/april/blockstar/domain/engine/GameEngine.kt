package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.ADD_TOOL_COST
import com.april.blockstar.domain.config.COINS_AWARD_TIER_1
import com.april.blockstar.domain.config.COINS_AWARD_TIER_2
import com.april.blockstar.domain.config.COINS_AWARD_TIER_3
import com.april.blockstar.domain.config.COINS_PER_CLEARED_LINE
import com.april.blockstar.domain.config.DELETE_TOOL_COST
import com.april.blockstar.domain.config.INITIAL_COINS
import com.april.blockstar.domain.config.MULTI_LINE_COIN_BONUS
import com.april.blockstar.domain.config.REFRESH_TOOL_COST
import com.april.blockstar.domain.config.SCORE_AWARD_TIER_1
import com.april.blockstar.domain.config.SCORE_AWARD_TIER_2
import com.april.blockstar.domain.config.SCORE_AWARD_TIER_3
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.GameState
import com.april.blockstar.domain.model.PendingBlock

data class PlacementResult(
    val state: GameState,
    val clearedLineCount: Int,
    val scoreAdded: Int,
    val coinsAdded: Int
)

data class ToolUseResult(
    val state: GameState,
    val clearedLineCount: Int = 0,
    val coinsDelta: Int = 0
)

data class SettlementResult(
    val state: GameState,
    val awardCoins: Int
)

class GameEngine(
    private val shapeGenerator: ShapeGenerator = ShapeGenerator()
) {
    fun newGame(
        highestScore: Int = 0,
        coins: Int = INITIAL_COINS,
        vibrationEnabled: Boolean = true
    ): GameState {
        val state = GameState.empty(
            highestScore = highestScore,
            coins = coins,
            vibrationEnabled = vibrationEnabled
        )
            .copy(pendingBlocks = shapeGenerator.generateRound())
        return state.copy(
            isGameOver = DeadlockDetector.isDeadlocked(state.board, state.pendingBlocks)
        )
    }

    fun normalizeRestoredState(state: GameState): GameState {
        val pendingBlocks = normalizePendingSlots(state.pendingBlocks)
        return state.copy(
            pendingBlocks = pendingBlocks,
            highestScore = maxOf(state.highestScore, state.score),
            coins = state.coins.coerceAtLeast(0),
            isGameOver = DeadlockDetector.isDeadlocked(state.board, pendingBlocks),
            hasActiveGame = true
        )
    }

    fun canPlace(state: GameState, pendingIndex: Int, row: Int, col: Int): Boolean {
        val block = state.pendingBlocks.getOrNull(pendingIndex) ?: return false
        return PlacementValidator.canPlace(state.board, block.shape, row, col)
    }

    fun placeBlock(state: GameState, pendingIndex: Int, row: Int, col: Int): PlacementResult? {
        val block = state.pendingBlocks.getOrNull(pendingIndex) ?: return null
        if (!PlacementValidator.canPlace(state.board, block.shape, row, col)) {
            return null
        }

        val placedBoard = state.board.place(block.shape, block.color, row, col)
        val clearResult = LineClearer.clearFullLines(placedBoard)
        val scoreAdded = ScoreCalculator.scoreForPlacement(
            placedCellCount = block.shape.cellCount,
            clearedLineCount = clearResult.clearedLineCount
        )
        val coinsAdded = coinsForClearedLines(clearResult.clearedLineCount)

        val pendingAfterPlacement = state.pendingBlocks.toMutableList().also {
            it[pendingIndex] = null
        }
        val nextPendingBlocks = if (pendingAfterPlacement.all { it == null }) {
            shapeGenerator.generateRound()
        } else {
            pendingAfterPlacement.toList()
        }

        val nextScore = state.score + scoreAdded
        val nextHighest = maxOf(state.highestScore, nextScore)
        val nextCoins = state.coins + coinsAdded
        val nextGameOver = DeadlockDetector.isDeadlocked(clearResult.board, nextPendingBlocks)

        return PlacementResult(
            state = state.copy(
                board = clearResult.board,
                pendingBlocks = nextPendingBlocks,
                score = nextScore,
                highestScore = nextHighest,
                coins = nextCoins,
                isGameOver = nextGameOver,
                hasActiveGame = true
            ),
            clearedLineCount = clearResult.clearedLineCount,
            scoreAdded = scoreAdded,
            coinsAdded = coinsAdded
        )
    }

    fun deleteCell(state: GameState, row: Int, col: Int): ToolUseResult? {
        if (state.coins < DELETE_TOOL_COST) return null
        if (!state.board.isInside(row, col) || !state.board.cellAt(row, col).isOccupied) return null

        val boardAfterDelete = state.board.removeCell(row, col)
        val clearResult = LineClearer.clearFullLines(boardAfterDelete)
        val nextState = state.copy(
            board = clearResult.board,
            coins = (state.coins - DELETE_TOOL_COST).coerceAtLeast(0),
            isGameOver = DeadlockDetector.isDeadlocked(clearResult.board, state.pendingBlocks),
            hasActiveGame = true
        )
        return ToolUseResult(
            state = nextState,
            clearedLineCount = clearResult.clearedLineCount,
            coinsDelta = -DELETE_TOOL_COST
        )
    }

    fun refreshRemainingBlocks(state: GameState): ToolUseResult? {
        if (state.coins < REFRESH_TOOL_COST) return null

        val remainingIndexes = state.pendingBlocks.mapIndexedNotNull { index, block ->
            if (block != null) index else null
        }
        if (remainingIndexes.isEmpty()) return null

        val replacements = shapeGenerator.generatePlaceableBlocks(
            board = state.board,
            count = remainingIndexes.size,
            preferSimpleShapes = true
        )
        if (replacements.size != remainingIndexes.size) return null

        val nextPendingBlocks = state.pendingBlocks.toMutableList()
        remainingIndexes.forEachIndexed { replacementIndex, pendingIndex ->
            nextPendingBlocks[pendingIndex] = replacements[replacementIndex]
        }

        val nextState = state.copy(
            pendingBlocks = nextPendingBlocks.toList(),
            coins = (state.coins - REFRESH_TOOL_COST).coerceAtLeast(0),
            isGameOver = DeadlockDetector.isDeadlocked(state.board, nextPendingBlocks),
            hasActiveGame = true
        )
        return ToolUseResult(state = nextState, coinsDelta = -REFRESH_TOOL_COST)
    }

    fun addBlock(state: GameState, shape: BlockShape, replaceIndex: Int? = null): ToolUseResult? {
        if (state.coins < ADD_TOOL_COST) return null
        if (!shapeGenerator.placeableShapes(state.board, listOf(shape)).contains(shape)) return null

        val targetIndex = state.pendingBlocks.indexOfFirst { it == null }.takeIf { it >= 0 }
            ?: replaceIndex?.takeIf { it in 0..2 }
            ?: return null

        val nextPendingBlocks = normalizePendingSlots(state.pendingBlocks).toMutableList()
        nextPendingBlocks[targetIndex] = PendingBlock(
            shape = shape,
            color = shapeGenerator.randomBlock(listOf(shape)).color
        )

        val nextState = state.copy(
            pendingBlocks = nextPendingBlocks.toList(),
            coins = (state.coins - ADD_TOOL_COST).coerceAtLeast(0),
            isGameOver = DeadlockDetector.isDeadlocked(state.board, nextPendingBlocks),
            hasActiveGame = true
        )
        return ToolUseResult(state = nextState, coinsDelta = -ADD_TOOL_COST)
    }

    fun addToolCandidates(state: GameState): List<BlockShape> {
        return shapeGenerator.placeableShapes(state.board, ShapeGenerator.addToolShapes)
    }

    fun finishGame(state: GameState): SettlementResult {
        val awardCoins = if (state.settlementAwardClaimed) 0 else settlementAwardForScore(state.score)
        val nextState = state.copy(
            coins = state.coins + awardCoins,
            highestScore = maxOf(state.highestScore, state.score),
            isGameOver = true,
            hasActiveGame = false,
            settlementAwardClaimed = true
        )
        return SettlementResult(state = nextState, awardCoins = awardCoins)
    }

    fun updateVibration(state: GameState, enabled: Boolean): GameState {
        return state.copy(vibrationEnabled = enabled)
    }

    private fun normalizePendingSlots(pendingBlocks: List<PendingBlock?>): List<PendingBlock?> {
        return List(3) { index -> pendingBlocks.getOrNull(index) }
    }

    private fun coinsForClearedLines(clearedLineCount: Int): Int {
        if (clearedLineCount <= 0) return 0
        return clearedLineCount * COINS_PER_CLEARED_LINE +
            if (clearedLineCount >= 2) MULTI_LINE_COIN_BONUS else 0
    }

    private fun settlementAwardForScore(score: Int): Int {
        return when {
            score >= SCORE_AWARD_TIER_3 -> COINS_AWARD_TIER_3
            score >= SCORE_AWARD_TIER_2 -> COINS_AWARD_TIER_2
            score >= SCORE_AWARD_TIER_1 -> COINS_AWARD_TIER_1
            else -> 0
        }
    }
}
