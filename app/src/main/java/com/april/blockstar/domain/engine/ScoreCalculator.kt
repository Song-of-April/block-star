package com.april.blockstar.domain.engine

import com.april.blockstar.domain.config.CLEAR_ONE_LINE_BONUS
import com.april.blockstar.domain.config.CLEAR_THREE_PLUS_LINES_BONUS
import com.april.blockstar.domain.config.CLEAR_TWO_LINES_BONUS

object ScoreCalculator {
    fun scoreForPlacement(placedCellCount: Int, clearedLineCount: Int): Int {
        val lineBonus = when (clearedLineCount) {
            0 -> 0
            1 -> CLEAR_ONE_LINE_BONUS
            2 -> CLEAR_TWO_LINES_BONUS
            else -> CLEAR_THREE_PLUS_LINES_BONUS
        }
        return placedCellCount + lineBonus
    }
}

