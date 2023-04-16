package com.advice.locations.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.Location
import com.advice.core.local.LocationRow
import com.advice.locations.ui.preview.LocationRowProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.SearchableTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreenView(
    containers: List<LocationRow>,
    onScheduleClicked: (LocationRow) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(topBar = {
        SearchableTopAppBar(
            title = { Text("Locations") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        ) { query ->

        }
    }) {
        LocationsScreenContent(containers, onScheduleClicked, modifier = Modifier.padding(it))
    }
}

@Composable
fun LocationsScreenContent(
    containers: List<LocationRow>,
    onScheduleClicked: (LocationRow) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier) {
        items(containers) {
            LocationView(
                it.title,
                null,
                it.status,
                it.hasChildren,
                it.isExpanded,
                it.depth
            ) { onScheduleClicked(it) }
        }
    }
}


@LightDarkPreview
@Composable
fun LocationsScreenViewPreview(
    @PreviewParameter(LocationRowProvider::class) location: LocationRow,
) {
    ScheduleTheme {
        LocationsScreenView(listOf(location), {

        }, {})
    }
}