package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    var state by remember(isBookmarked) {
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
                contentColor = contentColor,
            ),
    ) {
        Icon(icon, contentDescription = null)
    }
}

@PreviewLightDark
@Composable
private fun BookmarkButtonPreview() {
    ScheduleTheme {
        Row(Modifier.background(MaterialTheme.colorScheme.surface)) {
            BookmarkButton(
                isBookmarked = false,
                onCheckChange = {},
            )
            BookmarkButton(
                isBookmarked = true,
                onCheckChange = {},
            )
        }
    }
}
