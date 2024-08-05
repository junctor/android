package com.advice.wifi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.components.BackButton
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(
    state: WiFiScreenViewState,
    onBackPressed: () -> Unit,
    onConnectPressed: () -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                if (state is WiFiScreenViewState.Loaded) {
                    Text(state.wirelessNetwork.titleText)
                }
            },
            navigationIcon = {
                BackButton(onBackPressed)
            }
        )
    }) {
        WifiScreenContent(
            state,
            modifier = Modifier.padding(it),
            onConnectPressed = onConnectPressed,
        )
    }
}

@Composable
fun WifiScreenContent(
    state: WiFiScreenViewState,
    modifier: Modifier,
    onConnectPressed: () -> Unit
) {
    when (state) {
        WiFiScreenViewState.Error -> {
            Text("Error")
        }

        is WiFiScreenViewState.Loaded -> {
            Column(
                modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            ) {

                Text(state.wirelessNetwork.titleText)
                Text(state.wirelessNetwork.descriptionText)

                val certs = state.wirelessNetwork.certs ?: emptyList()
                for (cert in certs) {
                    Column {
                        Text(cert.name)
                        Text(cert.url)
                    }
                }

                Text("Result:")
                Text(state.result?.toString() ?: "No result")

                Button(
                    onClick = { onConnectPressed() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect to WiFi")
                }
            }
        }

        WiFiScreenViewState.Loading -> {
            ProgressSpinner()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WifiScreenViewPreview() {
    ScheduleTheme {
        WifiScreen(
            state = WiFiScreenViewState.Loading,
            onBackPressed = {},
            onConnectPressed = {},
        )
    }
}
