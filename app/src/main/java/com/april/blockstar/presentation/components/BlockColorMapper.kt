package com.april.blockstar.presentation.components

import androidx.compose.ui.graphics.Color
import com.april.blockstar.domain.model.BlockColor

fun BlockColor.toUiColor(): Color {
    return when (this) {
        BlockColor.Red -> Color(0xFFFF1828)
        BlockColor.Yellow -> Color(0xFFFFC400)
        BlockColor.Green -> Color(0xFF16D928)
        BlockColor.Blue -> Color(0xFF00BCEB)
        BlockColor.Purple -> Color(0xFFB42BEB)
    }
}

