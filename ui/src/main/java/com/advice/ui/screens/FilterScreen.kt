package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.FiltersScreenState
import com.advice.ui.components.Category
import com.advice.ui.components.SectionHeader
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.TagTypeProvider
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(state: FiltersScreenState, onClick: (Tag) -> Unit, onClear: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Filter") }, actions = {
                val showClearButton =
                    state is FiltersScreenState.Success && state.filters.flatMap { it.tags }
                        .any { it.isSelected }
                if (showClearButton) {
                    IconButton(onClear) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            })
        },
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Box(Modifier.padding(it)) {
            when (state) {
                FiltersScreenState.Init -> {
                }

                is FiltersScreenState.Success -> {
                    FilterScreenContent(state.filters, onClick = onClick)
                }
            }
        }
    }
}

@Composable
fun FilterScreenContent(
    tags: List<TagType>,
    modifier: Modifier = Modifier,
    onClick: (Tag) -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            Category(tag = Tag.bookmark, modifier = Modifier.fillMaxWidth()) {
                onClick(Tag.bookmark)
            }
        }
        for (tag in tags) {
            item {
                SectionHeader(tag.label)
            }
            for (tag in tag.tags) {
                item {
                    Category(tag, modifier = Modifier.fillMaxWidth()) {
                        onClick(tag)
                    }
                }
            }
        }
    }
}

@LightDarkPreview()
@Composable
private fun FilterScreenViewPreview(
    @PreviewParameter(TagTypeProvider::class) tagType: TagType
) {
    ScheduleTheme {
        val state = FiltersScreenState.Success(listOf(tagType))
        FilterScreen(state, {}, {})
    }
}
