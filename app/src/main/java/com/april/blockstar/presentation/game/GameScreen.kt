package com.april.blockstar.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.april.blockstar.domain.config.ADD_TOOL_COST
import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.config.DELETE_TOOL_COST
import com.april.blockstar.domain.config.REFRESH_TOOL_COST
import com.april.blockstar.domain.engine.ShapeGenerator
import com.april.blockstar.domain.model.PendingBlock
import com.april.blockstar.presentation.components.BlockPreview
import com.april.blockstar.presentation.components.GameBoard
import com.april.blockstar.presentation.components.PendingBlocksRow
import com.april.blockstar.presentation.components.ScoreHeader
import com.april.blockstar.presentation.components.StarBackground
import com.april.blockstar.presentation.components.ToolButtons
import com.april.blockstar.presentation.dialog.DeadlockDialog
import com.april.blockstar.presentation.dialog.PauseDialog
import com.april.blockstar.presentation.dialog.ReplaceBlockDialog
import com.april.blockstar.presentation.dialog.SettlementDialog
import com.april.blockstar.presentation.dialog.ShapePickerDialog
import com.april.blockstar.presentation.dialog.ToolConfirmDialog
import com.april.blockstar.presentation.theme.BlockStarTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private data class BoardLayoutInfo(
    val left: Float,
    val top: Float,
    val cellSize: Float
)

private data class DragState(
    val index: Int,
    val pointerPosition: Offset,
    val block: PendingBlock,
    val target: BoardTarget?,
    val isValid: Boolean
)

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    GameScreenContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        canPlace = viewModel::canPlace,
        modifier = modifier
    )
}

@Composable
private fun GameScreenContent(
    uiState: GameUiState,
    onAction: (GameAction) -> Unit,
    canPlace: (index: Int, row: Int, col: Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val hapticFeedback = LocalHapticFeedback.current

    var contentOriginInWindow by remember { mutableStateOf(Offset.Zero) }
    var boardLayout by remember { mutableStateOf<BoardLayoutInfo?>(null) }
    var dragState by remember { mutableStateOf<DragState?>(null) }

    fun updateDrag(index: Int, pointerPosition: Offset) {
        val block = uiState.pendingBlocks.getOrNull(index) ?: return
        val target = boardLayout?.let { layout ->
            resolveDragTarget(
                anchorX = pointerPosition.x,
                anchorY = pointerPosition.y,
                boardLeft = layout.left,
                boardTop = layout.top,
                cellSize = layout.cellSize,
                shapeWidth = block.shape.width,
                shapeHeight = block.shape.height
            )
        }
        val valid = target?.let { canPlace(index, it.row, it.col) } ?: false
        dragState = DragState(
            index = index,
            pointerPosition = pointerPosition,
            block = block,
            target = target,
            isValid = valid
        )
    }

    LaunchedEffect(uiState.messageId) {
        val message = uiState.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            delay(900)
            onAction(GameAction.DismissMessage)
        }
    }

    LaunchedEffect(uiState.praiseId) {
        if (uiState.praiseText != null) {
            delay(720)
            onAction(GameAction.DismissPraise)
        }
    }

    LaunchedEffect(uiState.vibrationEventId) {
        if (uiState.vibrationEventId > 0 && uiState.vibrationEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                onAction(GameAction.SaveNow)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { scaffoldPadding ->
        StarBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(WindowInsets.safeDrawing.asPaddingValues())
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .onGloballyPositioned { coordinates ->
                        contentOriginInWindow = coordinates.positionInWindow()
                    }
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ScoreHeader(
                            score = uiState.score,
                            highestScore = uiState.highestScore,
                            coins = uiState.coins,
                            onPauseClick = { onAction(GameAction.PauseGame) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        )

                        ToolButtons(
                            onRefreshClick = { onAction(GameAction.RequestRefreshTool) },
                            onDeleteClick = { onAction(GameAction.RequestDeleteTool) },
                            onAddClick = { onAction(GameAction.RequestAddTool) }
                        )

                        DeleteModeBar(
                            active = uiState.toolMode == ToolMode.Delete,
                            onCancel = { onAction(GameAction.CancelToolMode) }
                        )

                        val currentDrag = dragState
                        val preview = currentDrag?.target?.let { target ->
                            PlacementPreview(
                                block = currentDrag.block,
                                row = target.row,
                                col = target.col,
                                isValid = currentDrag.isValid
                            )
                        }

                        GameBoard(
                            board = uiState.board,
                            placementPreview = preview,
                            toolMode = uiState.toolMode,
                            dimmed = uiState.isGameOver &&
                                uiState.activeDialog == GameDialog.Deadlock &&
                                uiState.toolMode == ToolMode.None,
                            onCellClick = { row, col ->
                                onAction(GameAction.BoardCellClick(row, col))
                            },
                            onPositioned = { coordinates ->
                                val position = coordinates.positionInWindow()
                                boardLayout = BoardLayoutInfo(
                                    left = position.x,
                                    top = position.y,
                                    cellSize = coordinates.size.width.toFloat() / BOARD_SIZE
                                )
                            },
                            modifier = Modifier.fillMaxWidth(0.96f)
                        )

                        PendingBlocksRow(
                            pendingBlocks = uiState.pendingBlocks,
                            selectedBlockIndex = uiState.selectedBlockIndex,
                            draggingBlockIndex = dragState?.index,
                            onBlockClick = { index ->
                                onAction(GameAction.SelectPendingBlock(index))
                            },
                            onDragStart = { index, pointerPosition ->
                                updateDrag(index, pointerPosition)
                            },
                            onDragMove = { pointerPosition ->
                                val index = dragState?.index
                                if (index != null) {
                                    updateDrag(index, pointerPosition)
                                }
                            },
                            onDragEnd = {
                                val currentDrag = dragState
                                dragState = null
                                val target = currentDrag?.target
                                if (currentDrag != null && target != null && currentDrag.isValid) {
                                    onAction(GameAction.PlaceBlock(currentDrag.index, target.row, target.col))
                                }
                            },
                            onDragCancel = {
                                dragState = null
                            },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }

                    DragGhost(
                        dragState = dragState,
                        boardLayout = boardLayout,
                        contentOriginInWindow = contentOriginInWindow
                    )

                    PraiseText(
                        text = uiState.praiseText,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                ActiveDialog(
                    uiState = uiState,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun DeleteModeBar(
    active: Boolean,
    onCancel: () -> Unit
) {
    if (!active) return
    Row(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "删除模式",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = onCancel) {
            Text(text = "取消")
        }
    }
}

@Composable
private fun DragGhost(
    dragState: DragState?,
    boardLayout: BoardLayoutInfo?,
    contentOriginInWindow: Offset
) {
    val density = LocalDensity.current
    if (dragState == null || boardLayout == null) return

    val cellSizeDp = with(density) { boardLayout.cellSize.toDp() }
    val width = cellSizeDp * dragState.block.shape.width
    val height = cellSizeDp * dragState.block.shape.height
    val pointerInContent = dragState.pointerPosition - contentOriginInWindow
    val x = (pointerInContent.x - with(density) { width.toPx() } / 2f).roundToInt()
    val y = (pointerInContent.y - with(density) { height.toPx() } / 2f).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(x, y) }
            .size(width = width, height = height)
    ) {
        BlockPreview(
            block = dragState.block,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
private fun PraiseText(
    text: String?,
    modifier: Modifier = Modifier
) {
    if (text == null) return
    Text(
        text = text,
        modifier = modifier,
        color = Color(0xFFFFE45B),
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ActiveDialog(
    uiState: GameUiState,
    onAction: (GameAction) -> Unit
) {
    when (uiState.activeDialog) {
        GameDialog.Pause -> PauseDialog(
            vibrationEnabled = uiState.vibrationEnabled,
            onResumeClick = { onAction(GameAction.ResumeGame) },
            onRestartClick = { onAction(GameAction.RestartGame) },
            onToggleVibration = { onAction(GameAction.ToggleVibration) }
        )

        GameDialog.DeleteConfirm -> ToolConfirmDialog(
            title = "删除方块",
            message = "消耗 $DELETE_TOOL_COST 金币，随后点击棋盘上的一个已有方块。",
            confirmText = "进入删除",
            onConfirm = { onAction(GameAction.ConfirmDeleteTool) },
            onCancel = { onAction(GameAction.CancelDialog) }
        )

        GameDialog.RefreshConfirm -> ToolConfirmDialog(
            title = "刷新方块",
            message = "消耗 $REFRESH_TOOL_COST 金币，刷新所有尚未放置的方块。",
            confirmText = "刷新",
            onConfirm = { onAction(GameAction.ConfirmRefreshTool) },
            onCancel = { onAction(GameAction.CancelDialog) }
        )

        GameDialog.AddShapePicker -> ShapePickerDialog(
            shapes = uiState.addCandidateShapes,
            onShapeClick = { shapeId -> onAction(GameAction.SelectAddShape(shapeId)) },
            onCancel = { onAction(GameAction.CancelDialog) }
        )

        GameDialog.AddReplacePicker -> ReplaceBlockDialog(
            pendingBlocks = uiState.pendingBlocks,
            onReplaceClick = { index -> onAction(GameAction.ReplacePendingBlock(index)) },
            onCancel = { onAction(GameAction.CancelDialog) }
        )

        GameDialog.Deadlock -> DeadlockDialog(
            score = uiState.score,
            highestScore = uiState.highestScore,
            coins = uiState.coins,
            onDeleteClick = { onAction(GameAction.RequestDeleteTool) },
            onRefreshClick = { onAction(GameAction.RequestRefreshTool) },
            onAddClick = { onAction(GameAction.RequestAddTool) },
            onEndClick = { onAction(GameAction.EndCurrentGame) }
        )

        GameDialog.Settlement -> SettlementDialog(
            score = uiState.score,
            highestScore = uiState.highestScore,
            awardCoins = uiState.settlementAwardCoins,
            onRestartClick = { onAction(GameAction.RestartGame) }
        )

        null -> Unit
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun GameScreenPreview() {
    val generator = ShapeGenerator()
    BlockStarTheme {
        GameScreenContent(
            uiState = GameUiState(
                pendingBlocks = generator.generateRound(),
                isLoading = false
            ),
            onAction = {},
            canPlace = { _, _, _ -> false }
        )
    }
}
