package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.ui.HomeState
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.home.ConferenceSelector
import com.advice.ui.components.home.ConferenceView
import com.advice.ui.components.home.CountdownView
import com.advice.ui.components.home.HomeCard
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import java.util.Date

@Composable
fun HomeScreen(
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
private fun HomeScreenContent(
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
                ProgressSpinner()
            }

            null -> {
            }
        }
    }
}

@Composable
private fun HomeScreen(state: HomeState.Loaded, onNavigationClick: (String) -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ConferenceView(state.conference)

        val remainder = state.countdown
        if (remainder > 0L) {
            CountdownView(remainder)
        }

        state.menu.items.forEach {
            if (it is MenuItem.SectionHeading) {
                Label(text = it.label)
                return@forEach
            }

            if (it is MenuItem.Divider) {
                Box(
                    Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.outline)

                )
                return@forEach
            }

            HomeCard {
                Text(
                    it.label,
                    Modifier
                        .clickable {
                            when (it) {
                                is MenuItem.Document -> onNavigationClick("document/${it.documentId}")
                                is MenuItem.Menu -> onNavigationClick("menu/${it.label}/${it.menuId}")
                                is MenuItem.Navigation -> onNavigationClick("${it.function}/${it.label}")
                                is MenuItem.Organization -> onNavigationClick("organizations/${it.label}/${it.organizationId}")
                                is MenuItem.Schedule -> onNavigationClick(
                                    "schedule/${it.label}/${
                                        it.tags.joinToString(
                                            ","
                                        )
                                    }"
                                )

                                else -> error("Unknown menu item: $it")
                            }

                        }
                        .padding(16.dp)
                )
            }
        }

        // Required spacer to push content above the bottom bar
        Spacer(Modifier.height(64.dp))
    }
}

@LightDarkPreview
@Composable
private fun HomeScreenViewPreview() {
    ScheduleTheme {
        HomeScreen(
            state = HomeState.Loaded(
                conferences = listOf(Conference.Zero),
                conference = Conference.Zero,
                menu = Menu("Home", listOf()),
                countdown = Date().time / 1000L,
                forceTimeZone = false
            ),
            {}, {}
        )
    }
}
