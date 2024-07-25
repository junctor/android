package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.advice.wifi.suggestNetwork
import com.advice.wifi.ui.screens.WifiScreen


@Composable
internal fun Wifi(navController: NavHostController) {
    WifiScreen(
        onBackPressed = {
            navController.popBackStack()
        },
        onLinkClicked = {
            suggestNetwork(navController.context)
        },
    )
}
