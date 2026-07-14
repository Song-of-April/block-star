package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.CLEAR_BONUS

object ScoreCalculator {
    fun scoreForPlacement(placedCellCount: Int, clearedLineCount: Int): Int {
        return placedCellCount + if (clearedLineCount > 0) CLEAR_BONUS else 0
    }
}

