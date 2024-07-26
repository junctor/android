package com.advice.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.advice.core.utils.Storage

private val DarkColorPalette =
    darkColorScheme(
        primary = HotPink,
        inversePrimary = SugarHearts,
        secondary = CertainFrogs,
        onPrimary = Color.White,
        primaryContainer = HotPink,
        onPrimaryContainer = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        outline = Color.White.copy(alpha = 0.12f),
        errorContainer = Color(0xFFB00020),
    )

private val LightColorPalette =
    lightColorScheme(
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
fun ScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(Storage.KEY_PREFERENCES, Context.MODE_PRIVATE)
    val preference = preferences.getString("user_theme", "system")

    val colors =
        when {
            preference == "dark" -> DarkColorPalette
            preference == "light" -> LightColorPalette
            darkTheme -> DarkColorPalette
            else -> LightColorPalette
        }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
