package com.april.blockstar.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PauseDialog(
    vibrationEnabled: Boolean,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onToggleVibration: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResumeClick,
        title = { Text(text = "游戏暂停") },
        text = {
            Text(text = "当前振动：${if (vibrationEnabled) "开启" else "关闭"}")
        },
        confirmButton = {
            Button(onClick = onResumeClick) {
                Text(text = "继续")
            }
        },
        dismissButton = {
            TextButton(onClick = onToggleVibration) {
                Text(text = if (vibrationEnabled) "关闭振动" else "开启振动")
            }
            OutlinedButton(onClick = onRestartClick) {
                Text(text = "重新开始")
            }
        }
    )
}
