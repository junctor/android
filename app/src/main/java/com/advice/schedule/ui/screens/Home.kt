package com.advice.schedule.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.DragAnchors
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.navigation.Navigation
import com.advice.schedule.ui.navigation.navigate
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.ui.screens.FilterScreen
import com.advice.ui.screens.HomeScreen
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.ScheduleScreenState
import com.shortstack.core.R

@Composable
internal fun Home(navController: NavHostController) {
    val context = LocalContext.current
    val mainViewModel = viewModel<MainViewModel>()
    val viewState by mainViewModel.state.collectAsState()

    val homeViewModel = viewModel<HomeViewModel>()
    val filtersViewModel = viewModel<FiltersViewModel>()
    val scheduleViewModel = viewModel<ScheduleViewModel>()

    val scheduleScreenState = remember {
        scheduleViewModel.getState()
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    val homeState = homeViewModel.getHomeState().collectAsState(initial = HomeState.Loading).value
    val filtersScreenState =
        filtersViewModel.state.collectAsState(initial = FiltersScreenState.Init).value


    Box {
        OverlappingPanelsView(viewState.currentAnchor, leftPanel = {
            HomeScreen(state = homeState, onConferenceClick = {
                homeViewModel.setConference(it)
            }, onNavigationClick = {
                navController.navigate(it)
            }, onDismissNews = {
                homeViewModel.markLatestNewsAsRead(it)
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
                    navController.navigate(Navigation.Event(it.conference, it.id.toString()))
                },
                onBookmarkClick = { event, isBookmarked ->
                    scheduleViewModel.bookmark(event, isBookmarked)
                    (context as MainActivity).requestNotificationPermission()
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
                            id = R.drawable.logo_glitch
                        ), contentDescription = "Logo"
                    )
                }
                IconButton(onClick = {
                    navController.navigate(Navigation.Maps)
                }) {
                    Icon(
                        painterResource(
                            id = com.shortstack.hackertracker.R.drawable.ic_map_white_24dp
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
