package com.lawapp.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LawBlue = Color(0xFF1A237E)
val LawGold = Color(0xFFC5A059)
val LawWhite = Color(0xFFFFFFFF)
val LawDarkGray = Color(0xFF212121)

private val DarkColorScheme = darkColorScheme(
    primary = LawGold,
    secondary = LawBlue,
    tertiary = LawWhite,
    background = LawDarkGray,
    surface = LawDarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = LawBlue,
    secondary = LawGold,
    tertiary = LawBlue,
    background = LawWhite,
    surface = LawWhite
)

@Composable
fun LawAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
