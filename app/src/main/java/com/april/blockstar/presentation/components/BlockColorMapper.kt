package com.april.blockstar.presentation.components

import androidx.compose.ui.graphics.Color
import com.april.blockstar.domain.model.BlockColor

fun BlockColor.toUiColor(): Color {
    return when (this) {
        BlockColor.Red -> Color(0xFFE94B5F)
        BlockColor.Yellow -> Color(0xFFFFC547)
        BlockColor.Green -> Color(0xFF43D17D)
        BlockColor.Blue -> Color(0xFF45A7FF)
        BlockColor.Purple -> Color(0xFF9A68FF)
    }
}

