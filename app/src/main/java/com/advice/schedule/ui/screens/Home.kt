package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.navigation.toNavigation
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.MainViewModel
import com.advice.schedule.presentation.viewmodel.MainViewState
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.DragAnchors
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.components.SoundButton
import com.advice.ui.components.home.CountdownView
import com.advice.ui.screens.FilterScreen
import com.advice.ui.screens.HomeScreen
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.FiltersScreenState
import com.advice.ui.states.HomeState
import com.advice.ui.states.ScheduleScreenState
import org.koin.androidx.compose.koinViewModel

/** Matches Material3 bottom app bar height so panel content clears [DismissibleBottomAppBar]. */
private val BottomNavContentClearance = 80.dp

@Composable
internal fun Home(
    context: AppCompatActivity,
    navController: NavHostController,
) {
    val mainViewModel = koinViewModel<MainViewModel>(viewModelStoreOwner = context)
    val viewState by mainViewModel.state.collectAsState(MainViewState())

    val homeViewModel = koinViewModel<HomeViewModel>(viewModelStoreOwner = context)
    val filtersViewModel = koinViewModel<FiltersViewModel>(viewModelStoreOwner = context)
    val scheduleViewModel = koinViewModel<ScheduleViewModel>(viewModelStoreOwner = context)

    val scheduleScreenState =
        remember {
            scheduleViewModel.getState()
        }.collectAsState(initial = ScheduleScreenState.Loading).value

    val homeState = homeViewModel.getHomeState().collectAsState(initial = HomeState.Loading).value
    val filtersScreenState =
        filtersViewModel.state.collectAsState(initial = FiltersScreenState.Loading).value

    Box {
        OverlappingPanelsView(
            currentAnchor = viewState.currentAnchor,
            leftPanel = {
                HomeScreen(
                    state = homeState,
                    onConferenceClick = {
                        homeViewModel.setConference(it)
                    },
                    onNavigationClick = {
                        when (val navigation = it.toNavigation()) {
                            is Navigation.Schedule -> {
                                if (navigation.ids.isEmpty()) {
                                    mainViewModel.setAnchor(DragAnchors.Center)
                                    return@HomeScreen
                                }
                                navController.navigateTo(navigation)
                            }

                            is Navigation.Maps -> {
                                navController.navigateTo(navigation)
                            }

                            else -> {
                                navController.navigateTo(navigation)
                            }
                        }
                    },
                    onDismissNews = {
                        homeViewModel.markLatestNewsAsRead(it)
                    },
                    onRetry = {
                        homeViewModel.retry()
                    },
                    countdownContent = {
                        HomeCountdown(homeViewModel)
                    },
                    easterEgg =
                        if ((homeState as? HomeState.Loaded)?.hasEasterEgg == true) {
                            { SoundButton() }
                        } else {
                            null
                        },
                )
            },
            rightPanel = {
                FilterScreen(state = filtersScreenState, onClick = {
                    filtersViewModel.toggle(it)
                }, onClear = {
                    filtersViewModel.clearFilters()
                })
            },
            mainPanel = {
                ScheduleScreen(
                    state = scheduleScreenState,
                    onMenuClick = {
                        mainViewModel.setAnchor(DragAnchors.Start)
                    },
                    onFabClick = {
                        mainViewModel.setAnchor(DragAnchors.End)
                    },
                    onEventClick = {
                        // passing the content id and the session id
                        navController.navigateTo(
                            Navigation.Event(
                                it.conference,
                                it.content.id.toString(),
                                it.id.toString(),
                            ),
                        )
                    },
                    onBookmarkClick = { event, isBookmarked ->
                        scheduleViewModel.bookmark(event, isBookmarked)
                        (context as MainActivity).onBookmarkEvent()
                    },
                    onRetry = {
                        scheduleViewModel.retry()
                    },
                )
            },
            onPanelChangedListener = { panel ->
                mainViewModel.setAnchor(panel)
            },
            modifier =
                Modifier
                    .navigationBarsPadding()
                    .padding(bottom = if (viewState.isShown) BottomNavContentClearance else 0.dp),
        )
        DismissibleBottomAppBar(
            Modifier.align(Alignment.BottomCenter),
            isShown = viewState.isShown,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(
                    onClick = {
                        mainViewModel.setAnchor(DragAnchors.Center)
                    },
                ) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_event_note_24,
                        ),
                        contentDescription = "Schedule",
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigateTo(Navigation.Maps)
                    },
                ) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_map_24,
                        ),
                        contentDescription = "Maps",
                    )
                }
                IconButton(
                    onClick = { navController.navigateTo(Navigation.Search) },
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(
                    onClick = { navController.navigateTo(Navigation.Settings) },
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    }
}

@Composable
private fun HomeCountdown(homeViewModel: HomeViewModel) {
    val countdown by homeViewModel.getCountdown().collectAsState()
    if (countdown > 0L) {
        CountdownView(countdown)
    }
}
