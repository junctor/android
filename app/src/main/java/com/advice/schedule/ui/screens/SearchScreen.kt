package com.advice.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.organizations.ui.components.OrganizationRow
import com.advice.schedule.data.repositories.SearchState
import com.advice.schedule.utils.TimeUtils
import com.advice.ui.components.EventRowView
import com.advice.ui.components.SearchBar
import com.advice.ui.components.SpeakerView
import com.advice.ui.preview.LightDarkPreview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun SearchScreen(
    state: SearchState,
    onQueryChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Search") }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) {
        LazyColumn(Modifier.padding(it)) {
            item {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    SearchBar(modifier = Modifier) {
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
                        EventRowView(it.title, TimeUtils.getTimeStamp(context, it.start), it.location.name, it.types, it.isBookmarked)
                    }
                    if (state.results.speakers.isNotEmpty()) {
                        item {
                            HeaderRow("Speakers")
                        }
                    }
                    items(state.results.speakers) {
                        SpeakerView(it.name)
                    }
                    if (state.results.organizations.isNotEmpty()) {
                        item {
                            HeaderRow("Organizations")
                        }
                    }
                    items(state.results.organizations) {
                        // todo: fix this hacky solution
                        OrganizationRow(listOf(it))
                    }
                }
            }


        }
    }
}

@Composable
private fun HeaderRow(label: String) {
    Text(
        label, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), textAlign = TextAlign.Center
    )
}

@LightDarkPreview
@Composable
private fun SearchScreenPreview() {
}