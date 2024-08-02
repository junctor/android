package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.MapsViewModel
import com.advice.ui.states.MapsScreenState

@Composable
internal fun Maps(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<MapsViewModel>()
    val state = viewModel.state.collectAsState(initial = MapsScreenState.Loading).value
    com.advice.ui.screens.MapsScreen(
        state = state,
        onMapChange = {
                      viewModel.onMapChanged(it)
        },
        onBackPress = {
            navController.onBackPressed()
        },
    )
}
