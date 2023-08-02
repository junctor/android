package com.advice.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.advice.core.local.Conference
import com.advice.organizations.ui.components.OrganizationRow
import com.advice.schedule.data.repositories.SearchState
import com.advice.ui.components.EventRow
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.SearchBar
import com.advice.ui.components.Speaker
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.rememberScrollContext
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun SearchScreen(
    navController: NavController,
    conference: Conference?,
    state: SearchState?,
    onQueryChanged: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Search") }, navigationIcon = {
                IconButton(onClick = {
                    // clear the search
                    onQueryChanged("")
                    navController.popBackStack()
                }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.arrow_back
                        ),
                        contentDescription = "Back"
                    )
                }
            })
        }
    ) {
        val scrollState = rememberLazyListState()

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val focusRequested = remember { FocusRequester() }

        // Dismissing the keyboard when scrolling
        LaunchedEffect(key1 = scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex }.collect {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        }

        Box(Modifier.padding(it)) {
            if (state == null) {
                ProgressSpinner()
            } else {
                LazyColumn(state = scrollState) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            SearchBar(
                                query = (state as? SearchState.Results)?.results?.query ?: "",
                                placeholder = "Search " + (conference?.name ?: " anywhere"),
                                modifier = Modifier.focusRequester(focusRequested)
                            ) {
                                onQueryChanged(it)
                            }
                        }
                    }
                    when (state) {
                        SearchState.Idle -> {
                            // todo: show logo or something
                        }

                        is SearchState.Results -> {
                            if (state.results.events.isNotEmpty()) {
                                item {
                                    HeaderRow("Events")
                                }
                            }
                            items(state.results.events) {
                                EventRow(
                                    event = it,
                                    onEventPressed = {
                                        navController.navigate("event/${it.conference}/${it.id}")
                                    },
                                )
                            }
                            if (state.results.speakers.isNotEmpty()) {
                                item {
                                    HeaderRow("Speakers")
                                }
                            }
                            items(state.results.speakers) {
                                Speaker(
                                    name = it.name,
                                    title = it.title,
                                    onSpeakerClicked = {
                                        navController.navigate("speaker/${it.id}/${it.name}")
                                    }
                                )
                            }
                            if (state.results.organizations.isNotEmpty()) {
                                item {
                                    HeaderRow("Organizations")
                                }
                            }
                            state.results.organizations.windowed(
                                2,
                                2,
                                partialWindows = true
                            ) { organizations ->
                                item {
                                    OrganizationRow(organizations, onOrganizationPressed = {
                                        navController.navigate("organization/${it.id}")
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(label: String) {
    Label(label, modifier = Modifier.fillMaxWidth())
}

@LightDarkPreview
@Composable
private fun SearchScreenPreview() {
}
