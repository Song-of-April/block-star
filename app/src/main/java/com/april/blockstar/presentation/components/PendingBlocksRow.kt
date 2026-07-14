package com.april.blockstar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.april.blockstar.domain.model.PendingBlock

@Composable
fun PendingBlocksRow(
    pendingBlocks: List<PendingBlock?>,
    selectedBlockIndex: Int?,
    onBlockClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    draggingBlockIndex: Int? = null,
    onDragStart: (index: Int, pointerPosition: Offset) -> Unit = { _, _ -> },
    onDragMove: (pointerPosition: Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) { index ->
            PendingBlockSlot(
                block = pendingBlocks.getOrNull(index),
                selected = selectedBlockIndex == index,
                onClick = { onBlockClick(index) },
                dragging = draggingBlockIndex == index,
                onDragStart = { pointerPosition -> onDragStart(index, pointerPosition) },
                onDragMove = onDragMove,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PendingBlockSlot(
    block: PendingBlock?,
    selected: Boolean,
    onClick: () -> Unit,
    dragging: Boolean,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFFFFC547) else Color(0xFF2D8BA8)
    var positionInWindow by remember { mutableStateOf(Offset.Zero) }
    val dragModifier = if (block == null) {
        Modifier
    } else {
        Modifier.pointerInput(block) {
            detectDragGestures(
                onDragStart = { localOffset ->
                    onDragStart(positionInWindow + localOffset)
                },
                onDrag = { change, _ ->
                    change.consume()
                    onDragMove(positionInWindow + change.position)
                },
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel
            )
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1.15f)
            .alpha(if (dragging) 0.25f else 1f)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(Color(0x3328BFEA), RoundedCornerShape(8.dp))
            .onGloballyPositioned { coordinates ->
                positionInWindow = coordinates.positionInWindow()
            }
            .clickable(onClick = onClick)
            .then(dragModifier)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (block == null) {
            Text(
                text = "空",
                color = Color(0x99FFFFFF),
                fontWeight = FontWeight.Bold
            )
        } else {
            BlockPreview(
                block = block,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
