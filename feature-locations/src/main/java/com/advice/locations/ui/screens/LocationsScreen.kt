package com.advice.locations.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.LocationRow
import com.advice.locations.ui.components.Location
import com.advice.locations.ui.preview.LocationRowProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    containers: List<LocationRow>,
    onScheduleClicked: (LocationRow) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Locations") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
                }
            }
        )
    }) {
        LocationsScreenContent(containers, onScheduleClicked, modifier = Modifier.padding(it))
    }
}

@Composable
internal fun LocationsScreenContent(
    containers: List<LocationRow>,
    onScheduleClicked: (LocationRow) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier) {
        items(containers) {
            Location(
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
private fun LocationsScreenViewPreview(
    @PreviewParameter(LocationRowProvider::class) location: LocationRow,
) {
    ScheduleTheme {
        LocationsScreen(listOf(location), {

        }, {})
    }
}