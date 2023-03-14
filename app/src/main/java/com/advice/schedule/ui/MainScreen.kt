package com.advice.schedule.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.ui.screens.EventScreenView
import com.advice.ui.screens.HomeScreenView
import com.advice.ui.screens.ScheduleScreenView
import com.advice.ui.theme.ScheduleTheme
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenView(homeViewModel: HomeViewModel, scheduleViewModel: ScheduleViewModel) {
    ScheduleTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "schedule") {
            composable("home") {
                val state = homeViewModel.getHomeState().observeAsState()
                HomeScreenView(state.value) {
                    navController.navigate("schedule")
                }
            }
            composable("schedule") {
                val state = scheduleViewModel.state.collectAsState(initial = null).value

                ScheduleScreenView(
                    state = state,
                    onMenuClicked = { /*TODO*/ },
                    onSearchQuery = {},
                    onFabClicked = { /*TODO*/ },
                    onEventClick = {
                        navController.navigate("event/${it.id}")

                    },
                    onBookmarkClick = {}
                )
            }
//
            composable("event/{eventId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("eventId")
                val flatten =
                    scheduleViewModel.state.collectAsState(initial = null).value?.days?.values?.flatten()
                Timber.e("onCreate: ${flatten?.size} - $id",)
                val event = flatten?.find { it.id.toString() == id }

                if (event != null) {
                    EventScreenView(event, {
                        scheduleViewModel.bookmark(event)
                    }, {
//                                requireActivity().onBackPressed()
                    }, {
//                                (requireActivity() as MainActivity).showSchedule(event.location)
                    }, {
//                                (requireActivity() as MainActivity).showSpeaker(it)
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenViewPreview() {
    ScheduleTheme {
        //MainScreenView()
    }
}