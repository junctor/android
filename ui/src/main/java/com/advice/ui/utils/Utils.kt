package com.advice.ui.utils

import androidx.compose.ui.graphics.Color

fun parseColor(color: String?): Color {
    if (color == null)
        return Color.Blue

    return try {
        Color(android.graphics.Color.parseColor(color))
    } catch (ex: Exception) {
        Color.Green
    }
}