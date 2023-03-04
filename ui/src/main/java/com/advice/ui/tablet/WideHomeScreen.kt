package com.advice.ui.tablet

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.local.TagType
import com.advice.core.ui.HomeState
import com.advice.core.local.Article
import com.advice.core.local.Event
import com.advice.ui.screens.FilterScreenContent
import com.advice.ui.screens.HomeScreenContent
import com.advice.ui.screens.ScheduleScreenContent
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.SearchableTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WideHomeScreen(conference: Conference, articles: List<Article>, days: Map<String, List<Event>>, tags: List<TagType>, onEventClick: (Event) -> Unit, onBookmarkClick: (Event) -> Unit, modifier: Modifier = Modifier) {

    var isLeftPanelExpanded by remember {
        mutableStateOf(false)
    }

    var isRightPanelExpanded by remember {
        mutableStateOf(false)
    }


    Row(modifier.fillMaxSize()) {
        AnimatedVisibility(isLeftPanelExpanded) {
            val state = HomeState.Loaded(emptyList(), conference, articles)
            HomeScreenContent(state = state, modifier = Modifier.width(320.dp))
        }

        Column(
            Modifier
                .weight(1f)
        ) {
            Scaffold(topBar = {
                SearchableTopAppBar(title = { Text("Schedule") }, navigationIcon = {
                    IconButton(onClick = { isLeftPanelExpanded = !isLeftPanelExpanded }) {
                        Icon(Icons.Default.Menu, null)
                    }
                }, onSearchQuery = {})
            }, floatingActionButton = {
                FloatingActionButton(onClick = { isRightPanelExpanded = !isRightPanelExpanded }) {
                    Icon(Icons.Default.Search, null)
                }
            }) {
                ScheduleScreenContent(
                    days = days, onEventClick, onBookmarkClick, Modifier.padding(it)
                )
            }
        }

        AnimatedVisibility(isRightPanelExpanded) {
            FilterScreenContent(
                tags = tags, onClick = {}, modifier = Modifier
                    .width(320.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1080, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WideHomeScreenPreview() {
    ScheduleTheme {
        WideHomeScreen(Conference.Zero, emptyList(),  mapOf("Feb 3" to emptyList()), emptyList(), {}, {})
    }
}