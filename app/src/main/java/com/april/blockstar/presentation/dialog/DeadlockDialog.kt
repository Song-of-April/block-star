package com.april.blockstar.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.april.blockstar.domain.config.ADD_TOOL_COST
import com.april.blockstar.domain.config.DELETE_TOOL_COST
import com.april.blockstar.domain.config.REFRESH_TOOL_COST

@Composable
fun DeadlockDialog(
    score: Int,
    highestScore: Int,
    coins: Int,
    onDeleteClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onAddClick: () -> Unit,
    onEndClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "没有位置可以放置了") },
        text = {
            Text(text = "当前分数：$score\n最高分数：$highestScore\n金币：$coins")
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDeleteClick) {
                    Text(text = "删除  $DELETE_TOOL_COST")
                }
                Button(onClick = onRefreshClick) {
                    Text(text = "刷新  $REFRESH_TOOL_COST")
                }
                Button(onClick = onAddClick) {
                    Text(text = "增加  $ADD_TOOL_COST")
                }
                OutlinedButton(onClick = onEndClick) {
                    Text(text = "结束本局")
                }
            }
        }
    )
}
