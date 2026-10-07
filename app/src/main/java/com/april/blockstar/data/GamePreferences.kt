package com.april.blockstar.data

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.april.blockstar.domain.config.BOARD_SIZE
import com.april.blockstar.domain.config.INITIAL_COINS
import com.april.blockstar.domain.engine.GameEngine
import com.april.blockstar.domain.engine.ShapeGenerator
import com.april.blockstar.domain.model.BlockColor
import com.april.blockstar.domain.model.Board
import com.april.blockstar.domain.model.GameState
import com.april.blockstar.domain.model.PendingBlock
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class GamePreferences(context: Context) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile("block_star_save.preferences_pb") }
    )

    suspend fun loadGameState(engine: GameEngine): GameState {
        val preferences = dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .first()

        val highestScore = preferences[Keys.HIGHEST_SCORE] ?: 0
        val coins = (preferences[Keys.COINS] ?: INITIAL_COINS).coerceAtLeast(0)
        val vibrationEnabled = preferences[Keys.VIBRATION_ENABLED] ?: true
        val hasActiveGame = preferences[Keys.HAS_ACTIVE_GAME] ?: false

        if (!hasActiveGame) {
            return engine.newGame(
                highestScore = highestScore,
                coins = coins,
                vibrationEnabled = vibrationEnabled
            )
        }

        val restored = runCatching {
            GameState(
                board = decodeBoard(requireNotNull(preferences[Keys.BOARD])),
                pendingBlocks = decodePendingBlocks(requireNotNull(preferences[Keys.PENDING_BLOCKS])),
                score = preferences[Keys.CURRENT_SCORE] ?: 0,
                highestScore = highestScore,
                coins = coins,
                isGameOver = preferences[Keys.IS_GAME_OVER] ?: false,
                hasActiveGame = true,
                settlementAwardClaimed = preferences[Keys.SETTLEMENT_AWARDED] ?: false,
                vibrationEnabled = vibrationEnabled,
                refreshUses = preferences[Keys.REFRESH_USES] ?: 0,
                deleteUses = preferences[Keys.DELETE_USES] ?: 0,
                addUses = preferences[Keys.ADD_USES] ?: 0
            )
        }.getOrNull()

        return restored?.let(engine::normalizeRestoredState)
            ?: engine.newGame(
                highestScore = highestScore,
                coins = coins,
                vibrationEnabled = vibrationEnabled
            )
    }

    suspend fun saveGameState(state: GameState) {
        dataStore.edit { preferences ->
            preferences[Keys.HIGHEST_SCORE] = state.highestScore
            preferences[Keys.CURRENT_SCORE] = state.score
            preferences[Keys.COINS] = state.coins.coerceAtLeast(0)
            preferences[Keys.BOARD] = encodeBoard(state.board)
            preferences[Keys.PENDING_BLOCKS] = encodePendingBlocks(state.pendingBlocks)
            preferences[Keys.IS_GAME_OVER] = state.isGameOver
            preferences[Keys.HAS_ACTIVE_GAME] = state.hasActiveGame
            preferences[Keys.SETTLEMENT_AWARDED] = state.settlementAwardClaimed
            preferences[Keys.VIBRATION_ENABLED] = state.vibrationEnabled
            preferences[Keys.REFRESH_USES] = state.refreshUses
            preferences[Keys.DELETE_USES] = state.deleteUses
            preferences[Keys.ADD_USES] = state.addUses
        }
    }

    private fun encodeBoard(board: Board): String {
        return buildString(BOARD_SIZE * BOARD_SIZE) {
            repeat(BOARD_SIZE) { row ->
                repeat(BOARD_SIZE) { col ->
                    append(board.colorAt(row, col).toCode())
                }
            }
        }
    }

    private fun decodeBoard(value: String): Board {
        require(value.length == BOARD_SIZE * BOARD_SIZE)
        var index = 0
        val colors = List(BOARD_SIZE) {
            List(BOARD_SIZE) {
                value[index++].toBlockColor()
            }
        }
        return Board.fromColors(colors)
    }

    private fun encodePendingBlocks(pendingBlocks: List<PendingBlock?>): String {
        return List(3) { index ->
            val block = pendingBlocks.getOrNull(index)
            if (block == null) {
                "-"
            } else {
                "${block.shape.id},${block.color.toCode()}"
            }
        }.joinToString(";")
    }

    private fun decodePendingBlocks(value: String): List<PendingBlock?> {
        val slots = value.split(";")
        require(slots.size == 3)
        return slots.map { slot ->
            if (slot == "-") {
                null
            } else {
                val parts = slot.split(",")
                require(parts.size == 2)
                val shape = requireNotNull(ShapeGenerator.shapeById(parts[0]))
                val colorCode = require(parts[1].length == 1) { "Color code must be one char" }
                    .let { parts[1][0] }
                val color = requireNotNull(colorCode.toBlockColor())
                PendingBlock(shape = shape, color = color)
            }
        }
    }

    private fun BlockColor?.toCode(): Char {
        return when (this) {
            null -> '0'
            BlockColor.Red -> 'R'
            BlockColor.Yellow -> 'Y'
            BlockColor.Green -> 'G'
            BlockColor.Blue -> 'B'
            BlockColor.Purple -> 'P'
        }
    }

    private fun Char.toBlockColor(): BlockColor? {
        return when (this) {
            '0' -> null
            'R' -> BlockColor.Red
            'Y' -> BlockColor.Yellow
            'G' -> BlockColor.Green
            'B' -> BlockColor.Blue
            'P' -> BlockColor.Purple
            else -> error("Unknown block color code: $this")
        }
    }

    private object Keys {
        val HIGHEST_SCORE = intPreferencesKey("highest_score")
        val CURRENT_SCORE = intPreferencesKey("current_score")
        val COINS = intPreferencesKey("coins")
        val BOARD = stringPreferencesKey("board")
        val PENDING_BLOCKS = stringPreferencesKey("pending_blocks")
        val IS_GAME_OVER = booleanPreferencesKey("is_game_over")
        val HAS_ACTIVE_GAME = booleanPreferencesKey("has_active_game")
        val SETTLEMENT_AWARDED = booleanPreferencesKey("settlement_awarded")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val REFRESH_USES = intPreferencesKey("refresh_uses")
        val DELETE_USES = intPreferencesKey("delete_uses")
        val ADD_USES = intPreferencesKey("add_uses")
    }
}
