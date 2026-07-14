package com.april.blockstar

import androidx.compose.runtime.Composable
import com.april.blockstar.presentation.game.GameScreen
import com.april.blockstar.presentation.theme.BlockStarTheme

@Composable
fun BlockStarApp() {
    BlockStarTheme {
        GameScreen()
    }
}

