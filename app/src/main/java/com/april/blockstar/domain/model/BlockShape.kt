package com.april.blockstar.domain.model

data class BlockShape(
    val id: String,
    val name: String,
    val cells: List<CellOffset>
) {
    val width: Int = cells.maxOf { it.col } + 1
    val height: Int = cells.maxOf { it.row } + 1
    val cellCount: Int = cells.size
}

