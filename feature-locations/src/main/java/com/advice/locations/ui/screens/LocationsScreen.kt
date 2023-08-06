package com.advice.locations.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.LocationRow
import com.advice.core.local.LocationSchedule
import com.advice.core.local.LocationStatus
import com.advice.core.utils.TimeUtil
import com.advice.locations.ui.components.Location
import com.advice.locations.ui.components.LocationRow
import com.advice.locations.ui.preview.LocationRowProvider
import com.advice.ui.R
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    containers: List<LocationRow>,
    onToggleClicked: (LocationRow) -> Unit,
    onScheduleClicked: (LocationRow) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    var location by remember {
        mutableStateOf<LocationRow?>(null)
    }

    BottomSheetScaffold(
        sheetContent = {
            val row = location
            if (row != null) {
                ScheduleBottomSheet(context, row) {
                    onScheduleClicked(row)
                    location = null
                }
            }
        },
        scaffoldState = scaffoldState,
        sheetTonalElevation = 0.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Locations") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.arrow_back), null)
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            Modifier
        ) {
            LocationsScreenContent(containers,
                onScheduleClicked = {
                    if (it.hasChildren) {
                        onToggleClicked(it)
                    } else {
                        coroutineScope.launch {
                            location = it
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                }, onScroll = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                })
        }
    }
}

@Composable
private fun ScheduleBottomSheet(
    context: Context,
    row: LocationRow,
    onScheduleClicked: () -> Unit,
) {
    Column(
        Modifier
            .padding(start = 32.dp, end = 32.dp, bottom = 64.dp)
    ) {
        Text(
            row.title,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(16.dp)
        )
        for (schedule in row.schedule) {
            LocationRow(
                date = TimeUtil.getScheduleDateStamp(context, schedule),
                time = TimeUtil.getScheduleTimestamp(context, schedule)
            )
        }
        OutlinedButton(onClick = onScheduleClicked, Modifier.fillMaxWidth()) {
            Text("Open Schedule")
        }
    }
}

@Composable
internal fun LocationsScreenContent(
    containers: List<LocationRow>,
    onScheduleClicked: (LocationRow) -> Unit,
    modifier: Modifier = Modifier,
    onScroll: () -> Unit = {},
) {
    val state = rememberLazyListState()
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }
            .collect { onScroll() }
    }

    LazyColumn(modifier, state) {
        items(containers, key = { "${it.id}:${it.isExpanded}" }) {
            Location(
                it.title,
                null,
                it.status,
                it.hasChildren,
                it.isExpanded,
                it.depth
            ) { onScheduleClicked(it) }
        }
        item {
            Spacer(Modifier.height(64.dp))
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
        }, {}, {})
    }
}

@LightDarkPreview
@Composable
private fun BottomSheetPreview() {
    val context = LocalContext.current
    val row = LocationRow(
        1,
        "test",
        LocationStatus.Open,
        1,
        true,
        true,
        listOf(LocationSchedule(Instant.now(), Instant.now(), "test", "open"))
    )
    ScheduleTheme {
        ScheduleBottomSheet(context, row, {

        })
    }
}
