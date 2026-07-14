package com.april.blockstar.domain.model

import com.april.blockstar.domain.config.INITIAL_COINS

data class GameState(
    val board: Board,
    val pendingBlocks: List<PendingBlock?>,
    val score: Int,
    val highestScore: Int,
    val coins: Int,
    val isGameOver: Boolean,
    val hasActiveGame: Boolean = true,
    val settlementAwardClaimed: Boolean = false,
    val vibrationEnabled: Boolean = true
) {
    companion object {
        fun empty(
            highestScore: Int = 0,
            coins: Int = INITIAL_COINS,
            vibrationEnabled: Boolean = true
        ): GameState {
            return GameState(
                board = Board.empty(),
                pendingBlocks = emptyList(),
                score = 0,
                highestScore = highestScore,
                coins = coins,
                isGameOver = false,
                hasActiveGame = true,
                settlementAwardClaimed = false,
                vibrationEnabled = vibrationEnabled
            )
        }
    }
}
