package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.core.ui.ScheduleFilter
import com.advice.ui.R
import com.advice.ui.components.BackButton
import com.advice.ui.components.DayHeader
import com.advice.ui.components.DaySelectorView
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.EventRowView
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.toScheduleEventUi
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.rememberDaySelectorScrollState
import com.advice.ui.states.ScheduleScreenState
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.topRoundedCornerShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: ScheduleScreenState,
    onMenuClick: () -> Unit,
    onFabClick: () -> Unit,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Schedule")
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                },
            )
        },
        floatingActionButton = {
            if (state is ScheduleScreenState.Success && state.showFab) {
                FloatingActionButton(
                    shape = CircleShape, onClick = onFabClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_filter_list_24), "Filter Schedule",
                    )
                }
            }
        },
        modifier =
            Modifier
                .clip(topRoundedCornerShape),
    ) {
        ScheduleScreenContent(state, onEventClick, onBookmarkClick, Modifier.padding(it))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: ScheduleScreenState,
    label: String?,
    onBackPress: () -> Unit,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(label ?: "Schedule")
                },
                navigationIcon = {
                    BackButton(onBackPress)
                },
            )
        },
    ) {
        ScheduleScreenContent(state, onEventClick, onBookmarkClick, Modifier.padding(it))
    }
}

@Composable
private fun ScheduleScreenContent(
    state: ScheduleScreenState,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when (state) {
            is ScheduleScreenState.Error -> {
                EmptyMessage(message = "Schedule not found")
            }

            is ScheduleScreenState.Empty -> {
                EmptyMessage(message = state.message, title = "No events found")
            }

            ScheduleScreenState.Loading -> {
                ProgressSpinner()
            }

            is ScheduleScreenState.Success -> {
                ScheduleScreenContent(
                    state.filter,
                    state.days,
                    onEventClick,
                    onBookmarkClick,
                )
            }
        }
    }
}

@Composable
private fun ScheduleScreenContent(
    filter: ScheduleFilter,
    days: Map<String, List<Event>>,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Only scrolling when the list of unique events changes
    var hasScrolled by rememberSaveable(inputs = arrayOf(days.flatMap { it.value }.map { it.id })) {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val elements =
        remember(key1 = days) {
            days.flatMap { listOf(it.key) + it.value }
        }

    val dayKeys =
        remember(key1 = days) {
            days.keys.toList()
        }

    val headerIndices =
        remember(key1 = elements) {
            elements.mapIndexedNotNull { index, item ->
                index.takeIf { item is String }
            }
        }

    val daySelectorState = rememberDaySelectorScrollState(
        listState = listState,
        headerIndices = headerIndices,
    )

    val scheduleDays =
        remember(days, context) {
            days.toScheduleEventUi(context)
        }

    // Scrolling to the first event that is not started
    LaunchedEffect(key1 = days) {
        if (hasScrolled) return@LaunchedEffect

        hasScrolled = true
        val first = elements.indexOfFirst { it is Event && !it.hasStarted }
        if (first != -1) {
            if (first > 0 && elements[first - 1] is String) {
                listState.scrollToItem(first - 1)
            } else {
                listState.scrollToItem(first)
            }
        }
    }

    if (days.isNotEmpty()) {
        Column(modifier = modifier) {
            DaySelectorView(
                days = dayKeys,
                start = daySelectorState.highlight.start,
                end = daySelectorState.highlight.end,
                stripScrollStart = daySelectorState.settled.start,
                stripScrollEnd = daySelectorState.settled.end,
            ) {
                coroutineScope.launch {
                    val index = elements.indexOf(it)
                    if (index != -1) {
                        listState.scrollToItem(index)
                    }
                }
            }
            LazyColumn(state = listState) {
                for ((day, events) in scheduleDays) {
                    // Header
                    item(key = day) {
                        DayHeader(day)
                    }
                    // Events
                    for (row in events) {
                        item(key = row.event.session.id) {
                            EventRowView(
                                row = row,
                                onEventPressed = {
                                    onEventClick(row.event)
                                },
                                onBookmark = { isChecked ->
                                    onBookmarkClick(row.event, isChecked)
                                },
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    } else {
        val message = when (filter) {
            ScheduleFilter.Default -> {
                "Try adjusting your filters"
            }

            is ScheduleFilter.Location -> {
                "No events found in this location"
            }

            is ScheduleFilter.Tag -> {
                if (filter.id == Tag.bookmark.id) {
                    "Bookmark events to see them here"
                } else {
                    "No events found with selected tag"
                }
            }

            is ScheduleFilter.Tags -> {
                if (filter.ids?.contains(Tag.bookmark.id) == true) {
                    "Bookmark events to see them here"
                } else {
                    "No events found with selected tags"
                }
            }
        }
        EmptyMessage(
            message = message,
            title = "No events found",
        )
    }
}

@PreviewLightDark
@Composable
private fun ScheduleScreenPreview(@PreviewParameter(FakeEventProvider::class) event: Event) {
    ScheduleTheme {
        val state =
            ScheduleScreenState.Success(
                ScheduleFilter.Default,
                mapOf(
                    "May 19" to listOf(event),
                ),
                true,
            )

        ScheduleScreen(state, {}, {}, {}, { _, _ -> })
    }
}

@PreviewLightDark
@Composable
private fun ScheduleScreenEmptyPreview(@PreviewParameter(FakeEventProvider::class) @Suppress("UNUSED_PARAMETER") event: Event) {
    ScheduleTheme {
        val state =
            ScheduleScreenState.Success(
                ScheduleFilter.Tag(Tag.bookmark.id, Tag.bookmark.label),
                emptyMap(),
                true,
            )

        ScheduleScreen(state, {}, {}, {}, { _, _ -> })
    }
}
