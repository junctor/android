package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.ui.activity.MainActivity
import com.advice.wifi.presentation.viewmodel.WifiViewModel
import com.advice.wifi.ui.screens.WifiRoute
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun Wifi(
    navController: NavHostController,
    id: Long,
) {
    val context = LocalContext.current

    WifiRoute(
        viewModel = koinViewModel<WifiViewModel>(),
        id = id,
        hasWirelessPermissions = { (context as MainActivity).hasWirelessPermissions() },
        onRequestWirelessPermissions = { (context as MainActivity).requestWirelessPermissions() },
        onBackPressed = { navController.onBackPressed() },
    )
}
