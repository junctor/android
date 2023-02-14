package com.advice.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.Colors

@Composable
fun BookmarkButton(isBookmarked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    val icon = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder

    IconToggleButton(
        checked = isBookmarked,
        onCheckedChange = onCheckChanged,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContentColor = Colors.controlActive
        )
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkButtonPreview() {
    MaterialTheme {
        BookmarkButton(isBookmarked = false) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookmarkButtonCheckedPreview() {
    MaterialTheme {
        BookmarkButton(isBookmarked = true) {

        }
    }
}