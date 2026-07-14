package com.april.blockstar.domain.model

data class BoardCell(
    val color: BlockColor? = null
) {
    val isOccupied: Boolean
        get() = color != null
}

