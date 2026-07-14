package com.april.blockstar.presentation.game

import com.april.blockstar.domain.model.PendingBlock

data class PlacementPreview(
    val block: PendingBlock,
    val row: Int,
    val col: Int,
    val isValid: Boolean
)
