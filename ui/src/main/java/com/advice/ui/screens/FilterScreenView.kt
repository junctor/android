package com.advice.ui.screens

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
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.TagTypeProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.FilterHeaderView
import com.advice.ui.views.FilterView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenView(state: FiltersScreenState, onClick: (Tag) -> Unit, onClear: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Filter") }, actions = {
                val showClearButton = state is FiltersScreenState.Success && state.filters.flatMap { it.tags }.any { it.isSelected }
                if (showClearButton) {
                    IconButton(onClear) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            })
        },
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) { contentPadding ->
        when (state) {
            FiltersScreenState.Init -> {

            }

            is FiltersScreenState.Success -> {
                FilterScreenContent(state.filters, Modifier.padding(contentPadding), onClick)
            }
        }
    }
}

@Composable
fun FilterScreenContent(tags: List<TagType>, modifier: Modifier = Modifier, onClick: (Tag) -> Unit) {
    LazyColumn(modifier = modifier) {
        for (tag in tags) {
            item {
                FilterHeaderView(tag.label)
            }
            for (tag in tag.tags) {
                item {
                    FilterView(tag) {
                        onClick(tag)
                    }
                }
            }
        }
    }
}


@LightDarkPreview()
@Composable
fun FilterScreenViewPreview(
    @PreviewParameter(TagTypeProvider::class) tagType: TagType
) {
    ScheduleTheme {
        val state = FiltersScreenState.Success(listOf(tagType))
        FilterScreenView(state, {}, {})
    }
}