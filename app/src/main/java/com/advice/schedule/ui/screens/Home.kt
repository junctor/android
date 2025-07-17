package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.toNavigation
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.DragAnchors
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.schedule.ui.viewmodels.MainViewState
import com.advice.ui.screens.FilterScreen
import com.advice.ui.screens.HomeScreen
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.ScheduleScreenState

@Composable
internal fun Home(context: AppCompatActivity, navController: NavHostController) {
    val mainViewModel = viewModel<MainViewModel>()
    val viewState by mainViewModel.state.collectAsState(MainViewState())

    val homeViewModel = viewModel<HomeViewModel>(context)
    val filtersViewModel = viewModel<FiltersViewModel>(context)
    val scheduleViewModel = viewModel<ScheduleViewModel>(context)

    val scheduleScreenState = remember {
        scheduleViewModel.getState()
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    val homeState = homeViewModel.getHomeState().collectAsState(initial = HomeState.Loading).value
    val filtersScreenState =
        filtersViewModel.state.collectAsState(initial = FiltersScreenState.Loading).value

    Box {
        OverlappingPanelsView(viewState.currentAnchor, leftPanel = {
            HomeScreen(
                state = homeState, onConferenceClick = {
                    homeViewModel.setConference(it)
                }, onNavigationClick = {
                    when (val navigation = it.toNavigation()) {
                        is Navigation.Schedule -> {
                            if (navigation.ids.isEmpty()) {
                                mainViewModel.setAnchor(DragAnchors.Center)
                                return@HomeScreen
                            }
                            navController.navigate(navigation)
                        }

                        is Navigation.Maps -> {
                            navController.navigate(navigation)
                        }

                        else -> {
                            navController.navigate(navigation)
                        }
                    }
                }, onDismissNews = {
                    homeViewModel.markLatestNewsAsRead(it)
                },
                onDocumentClick = {
                    navController.navigate(Navigation.Document(it))
                })
        }, rightPanel = {
            FilterScreen(state = filtersScreenState, onClick = {
                filtersViewModel.toggle(it)
            }, onClear = {
                filtersViewModel.clearBookmarks()
            })
        }, mainPanel = {
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
                    navController.navigate(
                        Navigation.Event(
                            it.conference,
                            it.content.id.toString(),
                            it.id.toString()
                        )
                    )
                },
                onBookmarkClick = { event, isBookmarked ->
                    scheduleViewModel.bookmark(event, isBookmarked)
                    (context as MainActivity).onBookmarkEvent()
                },
            )
        }, onPanelChangedListener = { panel ->
            mainViewModel.setAnchor(panel)
        })
        DismissibleBottomAppBar(
            Modifier.align(Alignment.BottomCenter),
            isShown = viewState.isShown,
        ) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    mainViewModel.setAnchor(DragAnchors.Center)
                }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_event_note_24
                        ), contentDescription = "schedule"
                    )
                }
                IconButton(onClick = {
                    navController.navigate(Navigation.Maps)
                }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_map_24
                        ), contentDescription = "Maps"
                    )
                }
                IconButton(onClick = { navController.navigate(Navigation.Search) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { navController.navigate(Navigation.Settings) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    }
}
