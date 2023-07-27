package com.advice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = HotPink,
    inversePrimary = SugarHearts,
    secondary = CertainFrogs,
    onPrimary = Color.White,
    primaryContainer = HotPink,
    onSurface = Color.White,
    outline = Color.White.copy(alpha = 0.12f),
)

private val LightColorPalette = lightColorScheme(
    primary = HotPink,
    inversePrimary = SugarHearts,
    secondary = CertainFrogs,
    onPrimary = Color.White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ScheduleTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
