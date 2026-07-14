package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.PendingBlock

object DeadlockDetector {
    fun isDeadlocked(board: Board, pendingBlocks: List<PendingBlock?>): Boolean {
        val remainingBlocks = pendingBlocks.filterNotNull()
        if (remainingBlocks.isEmpty()) {
            return false
        }

        return remainingBlocks.none { block ->
            (0 until BOARD_SIZE).any { row ->
                (0 until BOARD_SIZE).any { col ->
                    PlacementValidator.canPlace(board, block.shape, row, col)
                }
            }
        }
    }
}

