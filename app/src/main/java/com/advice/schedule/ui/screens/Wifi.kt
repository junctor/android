package com.advice.schedule.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.ui.activity.MainActivity
import com.advice.wifi.ui.screens.WifiScreen

@Composable
internal fun Wifi(navController: NavHostController, id: Long) {
    val context = LocalContext.current

    val viewModel = viewModel<WifiViewModel>()
    LaunchedEffect(id) {
        viewModel.get(id)
    }

    val state = viewModel.state.collectAsState().value

    WifiScreen(
        state = state,
        onBackPressed = {
            navController.onBackPressed()
        },
        onConnectPressed = {
            val mainActivity = context as MainActivity
            if (!mainActivity.hasWirelessPermissions()) {
                Toast.makeText(context, "Please grant wireless permissions", Toast.LENGTH_SHORT)
                    .show()
                mainActivity.requestWirelessPermissions()
            } else {
                Toast.makeText(context, "Attempting to save wifi config", Toast.LENGTH_SHORT).show()
                viewModel.saveWifiConfig()
            }
        },
        onDisconnectPressed = {
            viewModel.disconnect()
        }
    )
}
