package com.advice.schedule.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val addNetworksLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        viewModel.onAddNetworksResult(activityResult.resultCode)
    }

    val state = viewModel.state.collectAsState().value
    var showLocationRationale by remember { mutableStateOf(false) }

    if (showLocationRationale) {
        AlertDialog(
            onDismissRequest = { showLocationRationale = false },
            title = { Text("Location for Wi‑Fi setup") },
            text = {
                Text(
                    "On this Android version, location access is required only to save the conference Wi‑Fi network. " +
                        "It is not used for tracking or advertising.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationRationale = false
                        (context as MainActivity).requestWirelessPermissions()
                    },
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationRationale = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    WifiScreen(
        state = state,
        onBackPressed = {
            navController.onBackPressed()
        },
        onConnectPressed = {
            val mainActivity = context as MainActivity
            if (!mainActivity.hasWirelessPermissions()) {
                showLocationRationale = true
            } else {
                viewModel.saveWifiConfig { intent ->
                    addNetworksLauncher.launch(intent)
                }
            }
        },
        onDisconnectPressed = {
            viewModel.disconnect()
        },
        onForceLocalCert = { forceLocal ->
            viewModel.forceLocalCert(forceLocal)
        }
    )
}
