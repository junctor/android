package com.advice.products.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.ui.components.Category
import com.advice.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductFilterBottomSheet(
    tagTypes: List<TagType>,
    onDismiss: () -> Unit,
    onClick: (Tag) -> Unit,
) {
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        tonalElevation = 0.dp,
    ) {
        LazyColumn {
            for (tagType in tagTypes) {
                item {
                    SectionHeader("Size preference")
                }
                items(tagType.tags.windowed(2, 2)) { tags ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (tag in tags) {
                            Category(tag, modifier = Modifier.weight(1f)) {
                                onClick(tag)
                            }
                        }
                    }
                }
            }
        }
    }
}
