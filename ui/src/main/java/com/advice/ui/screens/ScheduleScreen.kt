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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.TimeUtil
import com.advice.ui.R
import com.advice.ui.components.DayHeader
import com.advice.ui.components.DaySelectorView
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.EventRowView
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.rememberScrollContext
import com.advice.ui.states.ScheduleScreenState
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: ScheduleScreenState?,
    onMenuClicked: () -> Unit,
    onFabClicked: () -> Unit,
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

                    IconButton(onClick = onMenuClicked) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(shape = CircleShape, onClick = onFabClicked) {
                Icon(painterResource(R.drawable.baseline_filter_list_24), "Filter Schedule")
            }
        },
        modifier = Modifier
            .clip(roundedCornerShape)
    ) {
        ScheduleScreenContent(state, onEventClick, onBookmarkClick, Modifier.padding(it))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: ScheduleScreenState?,
    label: String?,
    onBackPressed: () -> Unit,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(label?.replace("\\", "/") ?: "Schedule")
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painterResource(id = R.drawable.arrow_back),
                            "Back"
                        )
                    }
                }
            )
        },
    ) {
        ScheduleScreenContent(state, onEventClick, onBookmarkClick, Modifier.padding(it))
    }
}

@Composable
private fun ScheduleScreenContent(
    state: ScheduleScreenState?,
    onEventClick: (Event) -> Unit,
    onBookmarkClick: (Event, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when (state) {
            is ScheduleScreenState.Error -> {
                EmptyMessage("Schedule not found")
            }

            null, ScheduleScreenState.Init -> {
            }

            ScheduleScreenState.Loading -> {
                ProgressSpinner()
            }

            is ScheduleScreenState.Success -> {
                ScheduleScreenContent(
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
    val scrollContext = rememberScrollContext(listState = listState)

    val elements = remember(key1 = days) {
        days.flatMap { listOf(it.key) + it.value }
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

    val temp = remember(key1 = elements) {
        elements.mapIndexed { index, any -> index to any }.filter { it.second is String }
            .map { it.first }
    }

    val start = temp.indexOfLast { it <= scrollContext.start }
    val end = temp.indexOfLast { it <= scrollContext.end }

    if (days.isNotEmpty()) {
        Column(modifier = modifier) {
            DaySelectorView(days = days.map { it.key }, start = start, end = end) {
                coroutineScope.launch {
                    val index = elements.indexOf(it)
                    if (index != -1) {
                        listState.scrollToItem(index)
                    }
                }
            }
            LazyColumn(state = listState) {
                for (day in days) {
                    // Header
                    item(key = day.key) {
                        DayHeader(day.key)
                    }
                    // Events
                    for (it in day.value) {
                        val event = it
                        item(key = it.id) {
                            EventRowView(
                                title = it.title,
                                time = TimeUtil.getTimeStamp(context, it.session),
                                location = it.session.location.name,
                                tags = it.types,
                                isBookmarked = it.isBookmarked,
                                onEventPressed = {
                                    onEventClick(it)
                                },
                                onBookmark = { isChecked ->
                                    onBookmarkClick(it, isChecked)
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
        EmptyMessage("Schedule not found")
    }
}

@Preview
@Composable
private fun ScheduleScreenPreview() {
    ScheduleTheme {
        val state = ScheduleScreenState.Success(
            ScheduleFilter.Default,
            mapOf(
                "May 19" to listOf(
                    Event(
                        conference = "THOTCON 0xC",
                        title = "DOORS OPEN 喝一杯",
                        description = "",
                        session = Session(
                            timeZone = "America/Chicago",
                            start = Instant.now(),
                            end = Instant.now(),
                            location = Location(
                                -1L,
                                "LOC://AUD - Track 1 / Первый Трек",
                                "Track 1 / Первый Трек",
                                "THOCON 0xC"
                            ),
                        ),
                        updated = Instant.now(),
                        speakers = emptyList(),
                        types = listOf(
                            Tag(
                                -1L,
                                "Misc",
                                "",
                                "#e73dd2",
                                -1
                            )
                        ),
                        urls = emptyList(),
                    )
                )
            )
        )

        ScheduleScreen(state, {}, {}, {}, { event, isBookmarked -> })
    }
}
