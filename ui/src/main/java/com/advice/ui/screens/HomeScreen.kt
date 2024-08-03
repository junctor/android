package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.ui.components.Label
import com.advice.ui.components.MenuIcon
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.home.ArticleView
import com.advice.ui.components.home.ConferenceSelector
import com.advice.ui.components.home.ConferenceView
import com.advice.ui.components.home.CountdownView
import com.advice.ui.components.home.HomeCard
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.topRoundedCornerShape
import java.util.Date

@Composable
fun HomeScreen(
    state: HomeState?,
    onConferenceClick: (Conference) -> Unit,
    onNavigationClick: (MenuItem) -> Unit,
    onDismissNews: (NewsArticle) -> Unit,
) {
    Scaffold(
        topBar = { ConferenceSelector(state as? HomeState.Loaded, onConferenceClick) },
        modifier = Modifier.clip(topRoundedCornerShape),
    ) { contentPadding ->
        HomeScreenContent(
            state,
            onNavigationClick,
            onDismissNews,
            modifier =
            Modifier
                .padding(contentPadding),
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeState?,
    onNavigationClick: (MenuItem) -> Unit,
    onDismissNews: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            is HomeState.Error -> {
            }

            is HomeState.Loaded -> {
                HomeScreen(state, onNavigationClick, onDismissNews)
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
private fun HomeScreen(
    state: HomeState.Loaded,
    onNavigationClick: (MenuItem) -> Unit,
    onDismissNews: (NewsArticle) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ConferenceView(state.conference)

        val remainder = state.countdown
        if (remainder > 0L) {
            CountdownView(remainder)
        }

        // Latest news
        val news = state.news
        if (news != null) {
            ArticleView(
                title = news.name,
                text = news.text,
                date = news.date,
            ) { onDismissNews(news) }
        }

        state.menu.items.forEach {
            when (it) {
                is MenuItem.SectionHeading -> {
                    Label(text = it.label)
                }

                is MenuItem.Divider -> {
                    Divider()
                }

                else -> {
                    MenuItem(it) {
                        onNavigationClick(it)
                    }
                }
            }
        }

        val menuItem = MenuItem.Navigation("wifi", "WiFi", "Connect to the conference WiFi", "wifi")
        MenuItem(menuItem) {
            onNavigationClick(menuItem)
        }

        // Required spacer to push content above the bottom bar
        Spacer(Modifier.height(124.dp))
    }
}

@Composable
private fun Divider() {
    Box(
        Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.outline),
    )
}

@Composable
private fun MenuItem(
    menuItem: MenuItem,
    onNavigationClick: () -> Unit,
) {
    HomeCard {
        Column(
            Modifier
                .clickable(onClick = onNavigationClick)
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MenuIcon(menuItem.icon)
                Text(menuItem.label)
            }
            val description = menuItem.description
            if (description != null) {
                Text(
                    description,
                    modifier =
                    Modifier
                        .padding(start = 0.dp, top = 8.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenViewPreview() {
    ScheduleTheme {
        HomeScreen(
            state =
            HomeState.Loaded(
                conferences = listOf(Conference.Zero),
                conference = Conference.Zero,
                menu = Menu(-1, "Home", listOf()),
                news = null,
                countdown = Date().time / 1000L,
                forceTimeZone = false,
            ),
            {},
            {},
        )
    }
}

@PreviewLightDark
@Composable
private fun MenuItemPreview() {
    ScheduleTheme {
        MenuItem(
            MenuItem.Navigation(
                label = "Menu Item",
                icon = "description",
                description = "This is a description",
                function = "news",
            ),
            {},
        )
    }
}
