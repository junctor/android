package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.schedule.navigation.onBackPressed
import com.advice.wifi.ui.screens.WifiScreen

@Composable
internal fun Wifi(navController: NavHostController) {
    val viewModel = viewModel<WifiViewModel>()
    val state = viewModel.state.collectAsState().value

    WifiScreen(
        state = state,
        onBackPressed = {
            navController.onBackPressed()
        },
        onConnectPressed = {
            viewModel.saveWifiConfig()
        },
        onStateUpdated = { state ->
            viewModel.updateState(state)
        }
    )
}
