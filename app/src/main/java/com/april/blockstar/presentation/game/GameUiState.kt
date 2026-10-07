package com.april.blockstar.presentation.game

import com.april.blockstar.domain.config.INITIAL_COINS
import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.BlockColor
import com.april.blockstar.domain.model.PendingBlock

enum class GameDialog {
    Pause,
    DeleteConfirm,
    RefreshConfirm,
    AddConfirm,
    BombConfirm,
    AddShapePicker,
    AddReplacePicker,
    Deadlock,
    Settlement
}

enum class ToolMode {
    None,
    Delete
}

data class GameUiState(
    val board: Board = Board.empty(),
    val pendingBlocks: List<PendingBlock?> = listOf(null, null, null),
    val selectedBlockIndex: Int? = null,
    val score: Int = 0,
    val highestScore: Int = 0,
    val coins: Int = INITIAL_COINS,
    val isGameOver: Boolean = false,
    val isLoading: Boolean = true,
    val activeDialog: GameDialog? = null,
    val toolMode: ToolMode = ToolMode.None,
    val addCandidateShapes: List<BlockShape> = emptyList(),
    val selectedAddShapeId: String? = null,
    val pendingBombIndex: Int? = null,
    val settlementAwardCoins: Int = 0,
    val vibrationEnabled: Boolean = true,
    val message: String? = null,
    val messageId: Long = 0L,
    val praiseText: String? = null,
    val praiseId: Long = 0L,
    val clearAnimationCells: Map<Pair<Int, Int>, BlockColor> = emptyMap(),
    val clearAnimationId: Long = 0L,
    val placementShineCells: Set<Pair<Int, Int>> = emptySet(),
    val placementShineId: Long = 0L,
    val vibrationEventId: Long = 0L,
    val refreshUses: Int = 0,
    val deleteUses: Int = 0,
    val addUses: Int = 0,
    val unplaceableBlockIndexes: Set<Int> = emptySet()
)
