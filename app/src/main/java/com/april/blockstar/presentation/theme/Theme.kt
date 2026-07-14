package com.april.blockstar.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    secondary = StarCyan,
    background = DeepNavy,
    surface = DeepNavy,
    onPrimary = SunGold,
    onSecondary = DeepNavy,
    onBackground = SunGold,
    onSurface = SunGold
)

private val DarkColorScheme = darkColorScheme(
    primary = StarCyan,
    secondary = SunGold,
    background = DeepNavy,
    surface = DeepNavy,
    onPrimary = DeepNavy,
    onSecondary = DeepNavy,
    onBackground = SunGold,
    onSurface = SunGold
)

@Composable
fun BlockStarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}

