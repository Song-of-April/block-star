package com.april.blockstar.presentation.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.april.blockstar.data.GamePreferences
import com.april.blockstar.domain.config.ADD_TOOL_COST
import com.april.blockstar.domain.config.DELETE_TOOL_COST
import com.april.blockstar.domain.config.REFRESH_TOOL_COST
import com.april.blockstar.domain.engine.GameEngine
import com.april.blockstar.domain.engine.ShapeGenerator
import com.april.blockstar.domain.engine.ToolUseResult
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.GameState
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val engine = GameEngine()
    private val preferences = GamePreferences(application.applicationContext)

    private var gameState: GameState = GameState.empty()
    private var messageCounter = 0L
    private var praiseCounter = 0L
    private var vibrationCounter = 0L

    var uiState by mutableStateOf(GameUiState())
        private set

    init {
        viewModelScope.launch {
            gameState = preferences.loadGameState(engine)
            uiState = gameState.toUiState(activeDialog = dialogForState(gameState))
        }
    }

    fun canPlace(pendingIndex: Int, row: Int, col: Int): Boolean {
        if (uiState.isLoading || uiState.activeDialog == GameDialog.Pause) return false
        if (gameState.isGameOver) return false
        return engine.canPlace(gameState, pendingIndex, row, col)
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.SelectPendingBlock -> selectPendingBlock(action.index)
            is GameAction.PlaceBlock -> placeBlock(action.index, action.row, action.col)
            is GameAction.PlaceSelectedBlock -> placeSelectedBlock(action.row, action.col)
            is GameAction.BoardCellClick -> boardCellClick(action.row, action.col)
            is GameAction.SelectAddShape -> selectAddShape(action.shapeId)
            is GameAction.ReplacePendingBlock -> replacePendingBlock(action.index)
            GameAction.RestartGame -> restartGame()
            GameAction.PauseGame -> pauseGame()
            GameAction.ResumeGame -> resumeGame()
            GameAction.RequestDeleteTool -> requestDeleteTool()
            GameAction.ConfirmDeleteTool -> confirmDeleteTool()
            GameAction.CancelToolMode -> cancelToolMode()
            GameAction.RequestRefreshTool -> requestRefreshTool()
            GameAction.ConfirmRefreshTool -> confirmRefreshTool()
            GameAction.RequestAddTool -> requestAddTool()
            GameAction.CancelDialog -> cancelDialog()
            GameAction.EndCurrentGame -> endCurrentGame()
            GameAction.ToggleVibration -> toggleVibration()
            GameAction.SaveNow -> persist()
            GameAction.DismissMessage -> uiState = uiState.copy(message = null)
            GameAction.DismissPraise -> uiState = uiState.copy(praiseText = null)
        }
    }

    private fun selectPendingBlock(index: Int) {
        if (!canInteractWithBoard()) return

        val block = gameState.pendingBlocks.getOrNull(index)
        if (block == null) {
            showMessage("这个槽位已经放完了")
            return
        }

        uiState = uiState.copy(
            selectedBlockIndex = index,
            toolMode = ToolMode.None,
            message = null
        )
    }

    private fun boardCellClick(row: Int, col: Int) {
        when (uiState.toolMode) {
            ToolMode.Delete -> deleteCell(row, col)
            ToolMode.None -> placeSelectedBlock(row, col)
        }
    }

    private fun placeSelectedBlock(row: Int, col: Int) {
        val selectedIndex = uiState.selectedBlockIndex
        if (selectedIndex == null) {
            if (canInteractWithBoard()) showMessage("先选择一个方块")
            return
        }
        placeBlock(selectedIndex, row, col)
    }

    private fun placeBlock(index: Int, row: Int, col: Int) {
        if (!canInteractWithBoard()) return

        val result = engine.placeBlock(gameState, index, row, col)
        if (result == null) {
            showMessage("这里放不下")
            return
        }

        gameState = result.state
        val praiseText = praiseForClearedLines(result.clearedLineCount)
        uiState = gameState.toUiState(
            selectedBlockIndex = null,
            activeDialog = dialogForState(gameState),
            message = buildPlacementMessage(result.scoreAdded, result.coinsAdded),
            messageId = ++messageCounter,
            praiseText = praiseText,
            praiseId = if (praiseText == null) uiState.praiseId else ++praiseCounter,
            vibrationEventId = nextVibrationEvent()
        )
        persist()
    }

    private fun requestDeleteTool() {
        if (uiState.isLoading) return
        if (gameState.coins < DELETE_TOOL_COST) {
            showMessage("金币不足，需要 $DELETE_TOOL_COST")
            return
        }
        if (!gameState.board.hasOccupiedCells()) {
            showMessage("棋盘上没有可删除的方块")
            return
        }
        uiState = uiState.copy(activeDialog = GameDialog.DeleteConfirm)
    }

    private fun confirmDeleteTool() {
        if (uiState.activeDialog != GameDialog.DeleteConfirm) return
        uiState = uiState.copy(
            activeDialog = null,
            toolMode = ToolMode.Delete,
            selectedBlockIndex = null,
            message = "点击棋盘上的一个方块",
            messageId = ++messageCounter
        )
    }

    private fun deleteCell(row: Int, col: Int) {
        val result = engine.deleteCell(gameState, row, col)
        if (result == null) {
            showMessage("请选择一个已有方块")
            return
        }
        applyToolResult(
            result = result,
            message = "已删除 -$DELETE_TOOL_COST",
            closeToolMode = true
        )
    }

    private fun cancelToolMode() {
        uiState = uiState.copy(
            toolMode = ToolMode.None,
            activeDialog = dialogForState(gameState),
            selectedBlockIndex = null
        )
    }

    private fun requestRefreshTool() {
        if (uiState.isLoading) return
        if (gameState.coins < REFRESH_TOOL_COST) {
            showMessage("金币不足，需要 $REFRESH_TOOL_COST")
            return
        }
        if (gameState.pendingBlocks.none { it != null }) {
            showMessage("当前没有可刷新的方块")
            return
        }
        uiState = uiState.copy(activeDialog = GameDialog.RefreshConfirm)
    }

    private fun confirmRefreshTool() {
        if (uiState.activeDialog != GameDialog.RefreshConfirm) return
        val result = engine.refreshRemainingBlocks(gameState)
        if (result == null) {
            showMessage("当前棋盘没有可刷新的可放置形状")
            uiState = uiState.copy(activeDialog = dialogForState(gameState))
            return
        }
        applyToolResult(
            result = result,
            message = "已刷新 -$REFRESH_TOOL_COST",
            closeToolMode = true
        )
    }

    private fun requestAddTool() {
        if (uiState.isLoading) return
        if (gameState.coins < ADD_TOOL_COST) {
            showMessage("金币不足，需要 $ADD_TOOL_COST")
            return
        }

        val candidates = engine.addToolCandidates(gameState)
        if (candidates.isEmpty()) {
            showMessage("当前棋盘没有可增加的形状")
            return
        }

        uiState = uiState.copy(
            activeDialog = GameDialog.AddShapePicker,
            addCandidateShapes = candidates,
            selectedAddShapeId = null
        )
    }

    private fun selectAddShape(shapeId: String) {
        if (uiState.activeDialog != GameDialog.AddShapePicker) return
        val shape = ShapeGenerator.shapeById(shapeId) ?: return

        if (gameState.pendingBlocks.any { it == null }) {
            val result = engine.addBlock(gameState, shape)
            if (result == null) {
                showMessage("无法增加这个方块")
                return
            }
            applyToolResult(
                result = result,
                message = "已增加 -$ADD_TOOL_COST",
                closeToolMode = true
            )
        } else {
            uiState = uiState.copy(
                activeDialog = GameDialog.AddReplacePicker,
                selectedAddShapeId = shape.id
            )
        }
    }

    private fun replacePendingBlock(index: Int) {
        if (uiState.activeDialog != GameDialog.AddReplacePicker) return
        val shape = uiState.selectedAddShapeId?.let(ShapeGenerator::shapeById) ?: return
        val result = engine.addBlock(gameState, shape, replaceIndex = index)
        if (result == null) {
            showMessage("无法替换这个槽位")
            return
        }
        applyToolResult(
            result = result,
            message = "已替换 -$ADD_TOOL_COST",
            closeToolMode = true
        )
    }

    private fun applyToolResult(
        result: ToolUseResult,
        message: String,
        closeToolMode: Boolean
    ) {
        gameState = result.state
        uiState = gameState.toUiState(
            activeDialog = dialogForState(gameState),
            toolMode = if (closeToolMode) ToolMode.None else uiState.toolMode,
            message = message,
            messageId = ++messageCounter,
            vibrationEventId = nextVibrationEvent()
        )
        persist()
    }

    private fun cancelDialog() {
        uiState = uiState.copy(
            activeDialog = dialogForState(gameState),
            addCandidateShapes = emptyList(),
            selectedAddShapeId = null
        )
    }

    private fun pauseGame() {
        if (uiState.isLoading) return
        uiState = uiState.copy(activeDialog = GameDialog.Pause)
        persist()
    }

    private fun resumeGame() {
        uiState = uiState.copy(activeDialog = dialogForState(gameState))
    }

    private fun restartGame() {
        gameState = engine.newGame(
            highestScore = gameState.highestScore,
            coins = gameState.coins,
            vibrationEnabled = gameState.vibrationEnabled
        )
        uiState = gameState.toUiState()
        persist()
    }

    private fun endCurrentGame() {
        val result = engine.finishGame(gameState)
        gameState = result.state
        uiState = gameState.toUiState(
            activeDialog = GameDialog.Settlement,
            settlementAwardCoins = result.awardCoins,
            message = if (result.awardCoins > 0) "结算奖励 +${result.awardCoins}" else null,
            messageId = if (result.awardCoins > 0) ++messageCounter else uiState.messageId
        )
        persist()
    }

    private fun toggleVibration() {
        gameState = engine.updateVibration(gameState, enabled = !gameState.vibrationEnabled)
        uiState = uiState.copy(vibrationEnabled = gameState.vibrationEnabled)
        persist()
    }

    private fun canInteractWithBoard(): Boolean {
        return !uiState.isLoading &&
            uiState.activeDialog == null &&
            uiState.toolMode == ToolMode.None &&
            !gameState.isGameOver
    }

    private fun buildPlacementMessage(scoreAdded: Int, coinsAdded: Int): String {
        return if (coinsAdded > 0) {
            "+$scoreAdded  金币 +$coinsAdded"
        } else {
            "+$scoreAdded"
        }
    }

    private fun praiseForClearedLines(clearedLineCount: Int): String? {
        if (clearedLineCount < 2) return null
        val words = listOf("好！", "赞！", "太棒了！")
        return words.random()
    }

    private fun dialogForState(state: GameState): GameDialog? {
        return if (state.isGameOver && state.hasActiveGame) GameDialog.Deadlock else null
    }

    private fun nextVibrationEvent(): Long {
        return if (gameState.vibrationEnabled) ++vibrationCounter else uiState.vibrationEventId
    }

    private fun showMessage(message: String) {
        uiState = uiState.copy(
            message = message,
            messageId = ++messageCounter
        )
    }

    private fun persist() {
        if (uiState.isLoading) return
        viewModelScope.launch {
            preferences.saveGameState(gameState)
        }
    }

    private fun GameState.toUiState(
        selectedBlockIndex: Int? = null,
        activeDialog: GameDialog? = null,
        toolMode: ToolMode = ToolMode.None,
        addCandidateShapes: List<BlockShape> = emptyList(),
        selectedAddShapeId: String? = null,
        settlementAwardCoins: Int = uiState.settlementAwardCoins,
        message: String? = null,
        messageId: Long = uiState.messageId,
        praiseText: String? = null,
        praiseId: Long = uiState.praiseId,
        vibrationEventId: Long = uiState.vibrationEventId
    ): GameUiState {
        return GameUiState(
            board = board,
            pendingBlocks = pendingBlocks,
            selectedBlockIndex = selectedBlockIndex,
            score = score,
            highestScore = highestScore,
            coins = coins,
            isGameOver = isGameOver,
            isLoading = false,
            activeDialog = activeDialog,
            toolMode = toolMode,
            addCandidateShapes = addCandidateShapes,
            selectedAddShapeId = selectedAddShapeId,
            settlementAwardCoins = settlementAwardCoins,
            vibrationEnabled = vibrationEnabled,
            message = message,
            messageId = messageId,
            praiseText = praiseText,
            praiseId = praiseId,
            vibrationEventId = vibrationEventId
        )
    }
}
