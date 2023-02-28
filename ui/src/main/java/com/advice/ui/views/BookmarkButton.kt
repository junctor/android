package com.advice.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BookmarkButton(isBookmarked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    var state by remember {
        mutableStateOf(isBookmarked)
    }

    val icon = if (state) Icons.Default.Favorite else Icons.Default.FavoriteBorder

    IconToggleButton(
        checked = state,
        onCheckedChange = {
            state = !state
            onCheckChanged(it)
        },
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContentColor = MaterialTheme.colorScheme.primary
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