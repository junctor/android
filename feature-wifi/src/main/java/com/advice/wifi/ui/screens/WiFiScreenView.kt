package com.advice.wifi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.components.BackButton
import com.advice.ui.theme.ScheduleTheme
import com.advice.wifi.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(
    state: WiFiScreenViewState,
    onBackPressed: () -> Unit,
    onConnectPressed: () -> Unit,
    onStateUpdated: (WiFiScreenViewState) -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("WiFi") },
            navigationIcon = {
                BackButton(onBackPressed)
            }
        )
    }) {
        WifiScreenContent(
            state,
            modifier = Modifier.padding(it),
            onConnectPressed = onConnectPressed,
            onStateUpdated = onStateUpdated,
        )
    }
}

@Composable
fun WifiScreenContent(
    state: WiFiScreenViewState,
    modifier: Modifier,
    onConnectPressed: () -> Unit,
    onStateUpdated: (WiFiScreenViewState) -> Unit
) {
    Column(
        modifier
            .padding(horizontal = 8.dp)
    ) {
        Column {
            ProfileRadioButton(Profile.AllowAny, selected = state.profile == Profile.AllowAny) {
                onStateUpdated(state.copy(profile = Profile.AllowAny))
            }
            ProfileRadioButton(Profile.LocalOnly, selected = state.profile == Profile.LocalOnly) {
                onStateUpdated(state.copy(profile = Profile.LocalOnly))
            }
            ProfileRadioButton(
                Profile.OutboundOnly,
                selected = state.profile == Profile.OutboundOnly
            ) {
                onStateUpdated(state.copy(profile = Profile.OutboundOnly))
            }
            ProfileRadioButton(Profile.Custom, selected = state.profile is Profile.Custom) {
                onStateUpdated(state.copy(profile = Profile.Custom))
            }
        }
        if (state.profile is Profile.Custom) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                TextField(value = state.ssid, onValueChange = {
                    onStateUpdated(state.copy(ssid = it))
                }, Modifier.fillMaxWidth(),
                    label = {
                        Text("SSID")
                    })

                TextField(value = state.username, onValueChange = {
                    onStateUpdated(state.copy(username = it))
                }, Modifier.fillMaxWidth(),
                    label = {
                        Text("Username")
                    })
                TextField(
                    value = state.password, onValueChange = {
                        onStateUpdated(state.copy(password = it))
                    }, Modifier.fillMaxWidth(),
                    label = {
                        Text("Password")
                    })
            }

        }

        Button(
            onClick = { onConnectPressed() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to WiFi")
        }
//        val text = stringResource(R.string.wifi_instructions)
//        Paragraph(
//            text,
//        )
    }
}

@Composable
private fun ProfileRadioButton(
    value: Profile,
    selected: Boolean,
    onProfileSelected: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(value.javaClass.simpleName, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = {
            onProfileSelected()
        })
    }
}

@Preview(showBackground = true)
@Composable
private fun WifiScreenViewPreview() {
    ScheduleTheme {
        WifiScreen(
            state = WiFiScreenViewState(
                profile = Profile.Custom,
                username = "defcon",
                password = "password",
            ),
            onBackPressed = {},
            onConnectPressed = {},
            onStateUpdated = {},
        )
    }
}
