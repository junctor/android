package com.advice.ui.components

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
import com.advice.ui.theme.ScheduleTheme

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onCheckChange: (Boolean) -> Unit,
) {
    var state by remember {
        mutableStateOf(isBookmarked)
    }

    val icon = if (state) Icons.Default.Favorite else Icons.Default.FavoriteBorder

    IconToggleButton(
        checked = state,
        onCheckedChange = {
            state = !state
            onCheckChange(it)
        },
        colors =
            IconButtonDefaults.iconToggleButtonColors(
                checkedContentColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkButtonPreview() {
    ScheduleTheme {
        BookmarkButton(
            isBookmarked = false,
            onCheckChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkButtonCheckedPreview() {
    ScheduleTheme {
        BookmarkButton(
            isBookmarked = true,
            onCheckChange = {},
        )
    }
}
