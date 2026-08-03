package com.advice.products.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.ui.components.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductFilterBottomSheet(
    tagTypes: List<TagType>,
    onDismiss: () -> Unit,
    onClick: (Tag) -> Unit,
    onClear: () -> Unit,
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    // MutableState (not `by`) so the Dialog content can read `.value` itself and
    // subscribe to updates — values captured from the parent composition go stale.
    val localTagTypes =
        remember {
            mutableStateOf(tagTypes.deepCopyTags())
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        tonalElevation = 0.dp,
    ) {
        val types = localTagTypes.value
        val hasSelection = types.any { type -> type.tags.any { it.isSelected } }
        val title = types.firstOrNull()?.label ?: "Filter"

        CenterAlignedTopAppBar(
            title = { Text(title) },
            actions = {
                if (hasSelection) {
                    TextButton(
                        onClick = {
                            localTagTypes.value = localTagTypes.value.clearSelections()
                            onClear()
                        },
                    ) {
                        Text("Clear filters")
                    }
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
        ) {
            for (tagType in types) {
                for (rowTags in tagType.tags.chunked(2)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (tag in rowTags) {
                            Category(
                                tag = tag,
                                modifier = Modifier.weight(1f),
                            ) {
                                localTagTypes.value =
                                    localTagTypes.value.toggleSelection(tag.id)
                                onClick(tag)
                            }
                        }
                        if (rowTags.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

private fun List<TagType>.deepCopyTags(): List<TagType> =
    map { type ->
        type.copy(tags = type.tags.map { it.copy() })
    }

private fun List<TagType>.toggleSelection(tagId: Long): List<TagType> =
    map { type ->
        type.copy(
            tags =
                type.tags.map { tag ->
                    if (tag.id == tagId) {
                        tag.copy(isSelected = !tag.isSelected)
                    } else {
                        tag
                    }
                },
        )
    }

private fun List<TagType>.clearSelections(): List<TagType> =
    map { type ->
        type.copy(tags = type.tags.map { it.copy(isSelected = false) })
    }
