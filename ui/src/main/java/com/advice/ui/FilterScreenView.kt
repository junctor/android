package com.advice.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.ui.FiltersScreenState
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.ui.views.FilterHeaderView
import com.advice.ui.views.FilterView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenView(state: FiltersScreenState, onClick: (FirebaseTag) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Filter") }, actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Close, null)
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
fun FilterScreenContent(tags: List<FirebaseTagType>, modifier: Modifier = Modifier, onClick: (FirebaseTag) -> Unit) {
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


@Preview(showBackground = true)
@Composable
fun FilterScreenViewPreview() {
    MaterialTheme {
//        val state = FiltersScreenState(listOf(FirebaseTag(label = "Talk", color_background = "#EE11AA")))
//        FilterScreenView(state)
    }
}