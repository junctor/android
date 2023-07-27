package com.advice.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.advice.organizations.ui.components.OrganizationRow
import com.advice.schedule.data.repositories.SearchState
import com.advice.ui.components.EventRow
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.SearchBar
import com.advice.ui.components.Speaker
import com.advice.ui.preview.LightDarkPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    navController: NavController,
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
                        painterResource(id = com.advice.ui.R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back"
                    )
                }
            })
        }
    ) {
        Box(Modifier.padding(it)) {
            if (state == null) {
                ProgressSpinner()
            } else {
                LazyColumn() {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            SearchBar("Search anywhere", modifier = Modifier) {
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
                            state.results.organizations.windowed(2, 2) { organizations ->
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