package com.april.blockstar.presentation.game

sealed interface GameAction {
    data class SelectPendingBlock(val index: Int) : GameAction
    data class PlaceBlock(val index: Int, val row: Int, val col: Int) : GameAction
    data class PlaceSelectedBlock(val row: Int, val col: Int) : GameAction
    data class BoardCellClick(val row: Int, val col: Int) : GameAction
    data class SelectAddShape(val shapeId: String) : GameAction
    data class ReplacePendingBlock(val index: Int) : GameAction
    data class BombPendingBlock(val index: Int) : GameAction
    data object RestartGame : GameAction
    data object PauseGame : GameAction
    data object ResumeGame : GameAction
    data object RequestDeleteTool : GameAction
    data object ConfirmDeleteTool : GameAction
    data object CancelToolMode : GameAction
    data object RequestRefreshTool : GameAction
    data object ConfirmRefreshTool : GameAction
    data object RequestAddTool : GameAction
    data object CancelDialog : GameAction
    data object EndCurrentGame : GameAction
    data object ToggleVibration : GameAction
    data object SaveNow : GameAction
    data object DismissMessage : GameAction
    data object DismissPraise : GameAction
}
