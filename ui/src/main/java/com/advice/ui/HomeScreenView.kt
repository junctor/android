package com.advice.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState

val roundedCornerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(state: HomeState?, onConferenceClick: (Conference) -> Unit) {
    Scaffold(
        topBar = { ConferenceSelector(state as? HomeState.Loaded, onConferenceClick) }, modifier = Modifier.clip(roundedCornerShape)
    ) { contentPadding ->
        HomeScreenContent(state, modifier = Modifier.padding(contentPadding), onConferenceClick)
    }
}

@Composable
fun HomeScreenContent(state: HomeState?, modifier: Modifier = Modifier, onConferenceClick: (Conference) -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            is HomeState.Error -> {}
            is HomeState.Loaded -> {


                ConferenceView(state.conference.name)
                // todo: CountdownView(state.conference.)
                LazyColumn {
                    items(state.article) {
                        ArticleView(text = it.text)
                    }
                }
            }

            HomeState.Loading -> {}
            null -> {}
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceSelector(state: HomeState.Loaded?, onConferenceClick: (Conference) -> Unit) {
    if (state == null) {
        CenterAlignedTopAppBar(title = { Text("Home") })
        return
    }

    var expanded by rememberSaveable {
        mutableStateOf(value = false)
    }

    Box(modifier = Modifier
        .clickable {
            expanded = !expanded
        }
        .fillMaxWidth()) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.conference.name)
            Icon(Icons.Default.ArrowDropDown, null)
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = {
        expanded = false
    }) {
        for (conference in state.conferences) {
            DropdownMenuItem(text = { Text(conference.name) }, onClick = {
                onConferenceClick(conference)
                expanded = false
            })
        }
    }
}

@Preview
@Composable
fun HomeScreenViewPreview() {
    MaterialTheme {
//        HomeScreenView(HomeState.Loaded(listOf("")))
    }
}

@Composable
fun ConferenceView(name: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(48.dp))
            Text(name)
        }
    }
}

@Composable
fun CountdownView() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("76 days")
            Text("05 hours")
            Text("56 minutes")
            Text("08 seconds")
        }
    }
}

@Composable
fun ArticleView(text: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text, modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
fun ConferenceViewPreview() {
    MaterialTheme {
        ConferenceView("DEFCON")
    }
}

@Preview
@Composable
fun CountdownViewPreview() {
    MaterialTheme {
        CountdownView()
    }
}

@Preview
@Composable
fun ArticleViewPreview() {
    MaterialTheme {
        ArticleView("Welcome to DEFCON 28!")
    }
}

@Preview
@Composable
fun ConferenceSelectorPreview() {
    MaterialTheme {
        //ConferenceSelector(state = )
    }
}