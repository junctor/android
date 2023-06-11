package com.advice.ui.utils

import androidx.compose.ui.graphics.Color
import com.advice.core.local.Tag

fun parseColor(color: String?): Color {
    if (color == null)
        return Color.Blue

    return try {
        Color(android.graphics.Color.parseColor(color))
    } catch (ex: Exception) {
        Color.Green
    }
}

fun createTag(label: String, color: String, isSelected: Boolean = false): Tag {
    return Tag(-1, label, "", color, -1, isSelected)
}
