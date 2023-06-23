package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.ui.components.home.*
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import com.shortstack.hackertracker.R
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    state: HomeState?,
    onConferenceClick: (Conference) -> Unit,
    onNavigationClick: (String) -> Unit,
) {
    Scaffold(
        topBar = { ConferenceSelector(state as? HomeState.Loaded, onConferenceClick) },
        modifier = Modifier.clip(roundedCornerShape)
    ) { contentPadding ->
        HomeScreenContent(
            state,
            onNavigationClick,
            modifier = Modifier
                .padding(contentPadding)
        )
    }
}

@Composable
fun HomeScreenContent(
    state: HomeState?,
    onNavigationClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            is HomeState.Error -> {
            }

            is HomeState.Loaded -> {
                HomeScreen(state, onNavigationClick)
            }

            HomeState.Loading -> {
                Image(painter = painterResource(id = R.drawable.skull), contentDescription = null)
            }

            null -> {
            }
        }

    }
}

@Composable
private fun HomeScreen(state: HomeState.Loaded, onNavigationClick: (String) -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        val conference = state.conference
        ConferenceView(
            conference.name,
            conference.startDate.toString(),
            conference.timezone,
            conference.description
        )

        if (state.hasWifi) {
            WiFiCard(onConnectClicked = {})
        }

        if (state.hasProducts) {
            ProductCard(media = "https://htem2.habemusconferencing.net/temp/dc24front.jpg") {
                onNavigationClick("merch")
            }
        }

        val remainder = state.countdown
        if (remainder > 0L) {
            CountdownView(remainder)
        }

        // Latest news
        state.news.take(1).forEach {
            ArticleView(text = it.text, date = it.date)
        }

        Text(
            "Documents",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // todo: replace with actual id
        HomeCard(Modifier.clickable {
            onNavigationClick("document/76")
        }) {
            Text("Code of Conduct", Modifier.padding(16.dp))
        }
        // todo: replace with actual id
        HomeCard(Modifier.clickable {
            onNavigationClick("document/77")
        }) {
            Text("Help & Support", Modifier.padding(16.dp))
        }

        Text(
            "Documents",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        HomeCard(Modifier.clickable {
            onNavigationClick("speakers")
        }) {
            Text("Speakers", Modifier.padding(16.dp))
        }
        HomeCard(Modifier.clickable {
            onNavigationClick("vendors")
        }) {
            Text("Vendors", Modifier.padding(16.dp))
        }


        HomeCard(Modifier.clickable {
            onNavigationClick("villages")
        }) {
            Text("Villages", Modifier.padding(16.dp))
        }
        HomeCard(Modifier.clickable {
            onNavigationClick("faq")
        }) {
            Text("FAQ", Modifier.padding(16.dp))
        }


        // Required spacer to push content above the bottom bar
        Spacer(Modifier.height(64.dp))
    }
}

@LightDarkPreview
@Composable
fun HomeScreenViewPreview() {
    ScheduleTheme {
        HomeScreenView(
            state = HomeState.Loaded(
                conferences = listOf(Conference.Zero),
                conference = Conference.Zero,
                news = emptyList(),
                countdown = Date().time / 1000L
            ), {}, {})
    }
}
