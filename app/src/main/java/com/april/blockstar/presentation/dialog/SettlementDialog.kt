package com.april.blockstar.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SettlementDialog(
    score: Int,
    highestScore: Int,
    awardCoins: Int,
    onRestartClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "本局结束") },
        text = {
            Text(text = "当前分数：$score\n最高分数：$highestScore\n金币奖励：$awardCoins")
        },
        confirmButton = {
            Button(onClick = onRestartClick) {
                Text(text = "重新开始")
            }
        }
    )
}
