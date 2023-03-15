package com.advice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

private val DarkColorPalette = darkColorScheme(
    primary = HotPink,
    inversePrimary = SugarHearts,
    secondary = CertainFrogs
)

private val LightColorPalette = lightColorScheme(
    primary = HotPink,
    inversePrimary = SugarHearts,
    secondary = CertainFrogs

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val UrlStyle = SpanStyle(color = SugarHearts, textDecoration = TextDecoration.Underline)
val BoldStyle = SpanStyle(fontWeight = FontWeight.Bold)
val PhoneNumberStyle = SpanStyle()
val EmailStyle = SpanStyle(color = SugarHearts, textDecoration = TextDecoration.Underline)
val HashTagStyle = SpanStyle(color = HotPink, fontWeight = FontWeight.Bold)

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