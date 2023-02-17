package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.utils.TimeUtil
import com.advice.schedule.models.local.Event
import com.advice.ui.ScrollContext
import com.advice.ui.rememberScrollContext
import com.advice.ui.views.DayHeaderView
import com.advice.ui.views.DaySelectorView
import com.advice.ui.views.EmptyView
import com.advice.ui.views.EventRowView
import kotlinx.coroutines.launch

sealed class ScheduleScreenState {
    object Init : ScheduleScreenState()
    object Loading : ScheduleScreenState()
    data class Success(val days: Map<String, List<Event>>) : ScheduleScreenState()
    data class Error(val error: String) : ScheduleScreenState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenView(
    state: ScheduleScreenState?,
    onMenuClicked: () -> Unit,
    onFabClicked: () -> Unit,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Schedule")
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClicked) {
                        Icon(Icons.Default.Menu, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(shape = CircleShape, onClick = onFabClicked) {
                Icon(Icons.Default.Search, null)
            }

        }, modifier = Modifier.clip(RoundedCornerShape(16.dp))
    )
    { contentPadding ->
        when (state) {
            is ScheduleScreenState.Error -> {
                EmptyView()
            }

            null,
            ScheduleScreenState.Init -> {
            }

            ScheduleScreenState.Loading -> {}
            is ScheduleScreenState.Success -> {
                ScheduleScreenContent(state.days, onEventClick, onBookmarkClick, modifier = Modifier.padding(contentPadding))
            }
        }
    }
}

@Composable
fun ScheduleScreenContent(days: Map<String, List<Event>>, onEventClick: (Event) -> Unit, onBookmarkClick: (Event) -> Unit, modifier: Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val scrollContext = rememberScrollContext(listState = listState)

    // todo: refactor, this is run too many times.
    val elements = days.flatMap { listOf(it.key) + it.value }
    val i = elements.mapIndexed { index, any -> index to any }.filter { it.second is String }.map { it.first }

    val start = i.indexOfLast { it <= scrollContext.start }
    val end = i.indexOfLast { it <= scrollContext.end }

    if (days.isNotEmpty()) {
        Column(modifier = modifier) {
            DaySelectorView(days = days.map { it.key }, start = start, end = end) {
                coroutineScope.launch {
                    listState.scrollToItem(elements.indexOf(it))
                }
            }
            LazyColumn(state = listState) {
                for (day in days) {
                    // Header
                    item {
                        DayHeaderView(day.key)
                    }
                    // Events
                    for (it in day.value) {
                        item {
                            EventRowView(
                                it.title,
                                TimeUtil.getTimeStamp(it.startTime, is24HourFormat = false),
                                it.location.name,
                                it.types,
                                it.isBookmarked,
                                modifier = Modifier.clickable {
                                    onEventClick(it)
                                }) { isChecked ->
                                onBookmarkClick(it)
                            }
                        }
                    }
                }
            }
        }
    } else {
        EmptyView("Schedule not found")
    }
}

@Preview
@Composable
fun ScheduleScreenViewPreview() {
    MaterialTheme {
        ScheduleScreenView(null, {}, {}, {}, {})
    }
}