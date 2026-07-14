package com.april.blockstar.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.april.blockstar.domain.model.BlockColor
import com.april.blockstar.domain.model.BlockShape
import com.april.blockstar.domain.model.PendingBlock
import com.april.blockstar.presentation.components.BlockPreview

@Composable
fun ShapePickerDialog(
    shapes: List<BlockShape>,
    onShapeClick: (String) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "增加方块") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                shapes.chunked(2).forEach { rowShapes ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowShapes.forEach { shape ->
                            Button(
                                onClick = { onShapeClick(shape.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .height(48.dp)
                                    ) {
                                        BlockPreview(
                                            block = PendingBlock(shape, BlockColor.Blue),
                                            modifier = Modifier.matchParentSize()
                                        )
                                    }
                                    Text(text = shape.name)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text(text = "取消")
            }
        }
    )
}

@Composable
fun ReplaceBlockDialog(
    pendingBlocks: List<PendingBlock?>,
    onReplaceClick: (Int) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "选择要替换的槽位") },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    OutlinedButton(
                        onClick = { onReplaceClick(index) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(54.dp)
                                    .height(44.dp)
                            ) {
                                val block = pendingBlocks.getOrNull(index)
                                if (block != null) {
                                    BlockPreview(block = block, modifier = Modifier.matchParentSize())
                                }
                            }
                            Text(text = "${index + 1}")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text(text = "取消")
            }
        }
    )
}
