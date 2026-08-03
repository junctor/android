package com.advice.search.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.local.Event
import com.advice.core.local.Organization
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.organizations.ui.components.OrganizationRow
import com.advice.search.data.repositories.SearchResults
import com.advice.search.data.repositories.SearchState
import com.advice.ui.components.BackButton
import com.advice.ui.components.CategorySize
import com.advice.ui.components.CategoryView
import com.advice.ui.components.EventRow
import com.advice.ui.components.FreqAskedQuestion
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.SearchBar
import com.advice.ui.glitch.GlitchLogo
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.Speaker as SpeakerView

@Composable
fun SearchScreen(
    conference: Conference?,
    state: SearchState?,
    onQueryChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onTagClicked: (Tag) -> Unit,
    onEventClicked: (Event) -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    onOrganizationClicked: (Organization) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequested = remember { FocusRequester() }
    // Local: Idle has no query, so Results-only binding clears characters while typing.
    var query by remember { mutableStateOf("") }

    val scrollState = rememberLazyListState()

    // Dismissing the keyboard when scrolling
    LaunchedEffect(key1 = scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }.collect {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            Row(
                Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                BackButton(
                    onClick = {
                        query = ""
                        onQueryChanged("")
                        onBackPressed()
                    },
                )

                SearchBar(
                    query = query,
                    placeholder = "Search " + (conference?.name ?: " anywhere"),
                    modifier = Modifier.focusRequester(focusRequested),
                ) {
                    query = it
                    onQueryChanged(it)
                }
            }
        },
    ) {
        Box(Modifier.padding(it)) {
            when (state) {
                null -> {
                    ProgressSpinner()
                }

                SearchState.Idle -> {
                    PlaceholderImage()
                }

                is SearchState.Results -> {
                    SearchResultsContent(
                        scrollState = scrollState,
                        results = state.results,
                        onTagClicked = onTagClicked,
                        onEventClicked = onEventClicked,
                        onSpeakerClicked = onSpeakerClicked,
                        onOrganizationClicked = onOrganizationClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    scrollState: LazyListState,
    results: SearchResults,
    onTagClicked: (Tag) -> Unit,
    onEventClicked: (Event) -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    onOrganizationClicked: (Organization) -> Unit,
) {
    LazyColumn(state = scrollState) {
        if (results.tags.isNotEmpty()) {
            item {
                HeaderRow("Tags")
            }
            item {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (tag in results.tags) {
                        CategoryView(
                            tag = tag,
                            size = CategorySize.Medium,
                            modifier =
                                Modifier.clickable {
                                    onTagClicked(tag)
                                },
                        )
                    }
                }
            }
        }

        if (results.faq.isNotEmpty()) {
            item {
                HeaderRow("FAQ")
            }
            items(results.faq) {
                FreqAskedQuestion(it.question, it.answer)
            }
        }

        if (results.events.isNotEmpty()) {
            item {
                HeaderRow("Events")
            }
        }
        items(results.events) { event ->
            EventRow(
                event = event,
                onEventPressed = {
                    onEventClicked(event)
                },
            )
        }
        if (results.speakers.isNotEmpty()) {
            item {
                HeaderRow("Speakers")
            }
        }
        items(results.speakers) { speaker ->
            SpeakerView(
                name = speaker.name,
                title = speaker.title,
                onSpeakerClicked = {
                    onSpeakerClicked(speaker)
                },
            )
        }
        if (results.organizations.isNotEmpty()) {
            item {
                HeaderRow("Organizations")
            }
        }
        results.organizations.windowed(
            2,
            2,
            partialWindows = true,
        ) { organizations ->
            item {
                OrganizationRow(organizations, onOrganizationPressed = {
                    onOrganizationClicked(it)
                })
            }
        }
    }
}

@Composable
private fun PlaceholderImage() {
    Box(Modifier.fillMaxSize()) {
        GlitchLogo(
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun HeaderRow(label: String) {
    Label(label, modifier = Modifier.fillMaxWidth())
}

@PreviewLightDark
@Composable
private fun SearchScreenPreview() {
    ScheduleTheme {
        SearchScreen(
            conference = Conference.Zero,
            state = SearchState.Idle,
            onQueryChanged = {},
            onBackPressed = {},
            onTagClicked = {},
            onEventClicked = {},
            onSpeakerClicked = {},
            onOrganizationClicked = {},
        )
    }
}
