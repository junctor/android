package com.advice.schedule.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.advice.core.local.Conference
import com.advice.organizations.ui.components.OrganizationRow
import com.advice.schedule.data.repositories.SearchResults
import com.advice.schedule.data.repositories.SearchState
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate
import com.advice.ui.components.EventRow
import com.advice.ui.components.FreqAskedQuestion
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.SearchBar
import com.advice.ui.components.Speaker
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.core.R
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SearchScreen(
    navController: NavController,
    conference: Conference?,
    state: SearchState?,
    onQueryChanged: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequested = remember { FocusRequester() }

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
                IconButton(onClick = {
                    // clear the search
                    onQueryChanged("")
                    navController.popBackStack()
                }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.arrow_back,
                        ),
                        contentDescription = "Back",
                    )
                }

                SearchBar(
                    query = (state as? SearchState.Results)?.results?.query ?: "",
                    placeholder = "Search " + (conference?.name ?: " anywhere"),
                    modifier = Modifier.focusRequester(focusRequested),
                ) {
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
                    SearchResults(scrollState, state.results, navController)
                    Timber.d("Results: ${state.results}")
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    scrollState: LazyListState,
    results: SearchResults,
    navController: NavController,
) {
    LazyColumn(state = scrollState) {
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
        items(results.events) {
            EventRow(
                event = it,
                onEventPressed = {
                    navController.navigate(
                        Navigation.Event(
                            it.conference,
                            it.content.id.toString(),
                            it.id.toString()
                        )
                    )
                },
            )
        }
        if (results.speakers.isNotEmpty()) {
            item {
                HeaderRow("Speakers")
            }
        }
        items(results.speakers) {
            Speaker(
                name = it.name,
                title = it.title,
                onSpeakerClicked = {
                    navController.navigate(Navigation.Speaker(it.id, it.name))
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
                    navController.navigate(Navigation.Organization(it.id))
                })
            }
        }
    }
}

@Composable
private fun PlaceholderImage() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo_glitch),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center),
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
            navController = rememberNavController(),
            conference =
            Conference.Zero,
            state = SearchState.Idle,
            onQueryChanged = {
            },
        )
    }
}
