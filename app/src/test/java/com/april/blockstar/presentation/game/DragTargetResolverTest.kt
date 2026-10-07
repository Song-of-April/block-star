package com.april.blockstar.presentation.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DragTargetResolverTest {
    @Test
    fun resolvesCenteredShapeToExpectedAnchorCell() {
        val target = resolveDragTarget(
            anchorX = 150f,
            anchorY = 150f,
            boardLeft = 0f,
            boardTop = 0f,
            cellSize = 30f,
            shapeWidth = 3,
            shapeHeight = 1
        )

        assertEquals(BoardTarget(row = 5, col = 4), target)
    }

    @Test
    fun returnsNullWhenPointerAnchorIsOutsideBoard() {
        val target = resolveDragTarget(
            anchorX = -1f,
            anchorY = 30f,
            boardLeft = 0f,
            boardTop = 0f,
            cellSize = 30f,
            shapeWidth = 1,
            shapeHeight = 1
        )

        assertNull(target)
    }

    @Test
    fun resolvesEvenWidthShapeWithoutOneCellDrift() {
        val target = resolveDragTarget(
            anchorX = 150f,
            anchorY = 150f,
            boardLeft = 0f,
            boardTop = 0f,
            cellSize = 30f,
            shapeWidth = 2,
            shapeHeight = 2
        )

        assertEquals(BoardTarget(row = 4, col = 4), target)
    }

    @Test
    fun rejectsExactBottomRightBoundary() {
        val target = resolveDragTarget(
            anchorX = 300f,
            anchorY = 300f,
            boardLeft = 0f,
            boardTop = 0f,
            cellSize = 30f,
            shapeWidth = 1,
            shapeHeight = 1
        )

        assertNull(target)
    }
}
