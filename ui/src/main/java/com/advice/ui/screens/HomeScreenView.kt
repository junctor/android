package com.advice.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import com.advice.ui.components.home.*
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    state: HomeState?,
    onConferenceClick: (Conference) -> Unit,
    onMerchClick: () -> Unit,
) {
    Scaffold(
        topBar = { ConferenceSelector(state as? HomeState.Loaded, onConferenceClick) },
        modifier = Modifier.clip(roundedCornerShape)
    ) { contentPadding ->
        HomeScreenContent(
            state,
            onMerchClick,
            modifier = Modifier
                .padding(contentPadding)
        )
    }
}

@Composable
fun HomeScreenContent(state: HomeState?, onMerchClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            is HomeState.Error -> {}
            is HomeState.Loaded -> {
                LazyColumn {
                    item {
                        ConferenceView(state.conference.name)
                    }
                    item {
                        MerchCardView(onMerchClick)
                    }

                    val remainder = state.countdown
                    if (remainder > 0L) {
                        item {
                            CountdownView(remainder)
                        }
                    }

                    items(state.article) {
                        ArticleView(text = it.text)
                    }
                    item {
                        Spacer(Modifier.height(64.dp))
                    }
                }
            }

            HomeState.Loading -> {}
            null -> {}
        }

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
                article = emptyList(),
                countdown = Date().time / 1000L
            ), onConferenceClick = {}, onMerchClick = {})
    }
}
