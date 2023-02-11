package com.advice.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.utils.Response
import com.advice.schedule.models.local.Event
import com.advice.ui.views.DaySelectorView
import com.advice.ui.views.EventRowView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenView(state: Response<List<Event>>?, onEventClick: (Event) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Schedule")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Menu, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Search, null)
            }

        }, modifier = Modifier.clip(RoundedCornerShape(16.dp))
    )
    { contentPadding ->
        when (state) {
            Response.Loading -> {

            }

            is Response.Success<List<Event>> -> {
                ScheduleScreenContent(state.data, onEventClick, modifier = Modifier.padding(contentPadding))
            }

            is Response.Error -> {

            }

            Response.Init -> {

            }

            null -> {

            }
        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenView() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Schedule")
                },
                navigationIcon = {
                    Icon(Icons.Default.Menu, null)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Search, null)
            }

        }, modifier = Modifier.clip(RoundedCornerShape(16.dp))
    )
    { contentPadding ->
        ScheduleScreenContent(emptyList(), {

        }, modifier = Modifier.padding(contentPadding))
    }
}

@Composable
fun ScheduleScreenContent(events: List<Event>, onEventClick: (Event) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        DaySelectorView(days = listOf("May 31", "June 1", "June 2", "June 3"))
        LazyColumn {
            items(events) {
                EventRowView(it.title, it.location.name, it.types, modifier = Modifier.clickable {
                    onEventClick(it)
                })
            }
        }
    }
}

@Preview
@Composable
fun ScheduleScreenViewPreview() {
    MaterialTheme {
        ScheduleScreenView()
    }
}