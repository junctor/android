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
import com.advice.core.local.wifi.WifiCertificate
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.ui.components.BackButton
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(
    state: WiFiScreenViewState,
    onBackPressed: () -> Unit,
    onConnectPressed: () -> Unit,
    onDisconnectPressed: () -> Unit,
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
            onDisconnectPressed = onDisconnectPressed,
        )
    }
}

@Composable
fun WifiScreenContent(
    state: WiFiScreenViewState,
    modifier: Modifier,
    onConnectPressed: () -> Unit,
    onDisconnectPressed: () -> Unit,
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

                Text(state.wirelessNetwork.ssid)

                if (state.wirelessNetwork.titleText.isNotBlank())
                    Text(state.wirelessNetwork.titleText)
                if (state.wirelessNetwork.descriptionText.isNotBlank())
                    Text(state.wirelessNetwork.descriptionText)

                val certs = state.wirelessNetwork.certs ?: emptyList()
                for (cert in certs) {
                    Column {
                        Text(cert.name)
                        Text(cert.url)
                    }
                }

                Column {
                    Text("Result:")
                    Text(state.result?.toString() ?: "-")
                }

                Button(
                    onClick = { onConnectPressed() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = roundedCornerShape,
                ) {
                    Text("Connect to WiFi")
                }

                Button(
                    onClick = { onDisconnectPressed() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = roundedCornerShape,
                ) {
                    Text("Disconnect from WiFi")
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
            state = WiFiScreenViewState.Loaded(
                wirelessNetwork = WirelessNetwork(
                    "anonymousIdentity",
                    autoJoin = "N",
                    certs = listOf(
                        WifiCertificate(1, "Cert 1", "https://cert1.com"),
                    ),
                    descriptionText = "Description",
                    disableAssociationMacRandomization = "N",
                    disableCaptiveNetworkDetection = "N",
                    eapMethod = "eapMethod",
                    eapSubjects = emptyList(),
                    enableIpv6 = "N",
                    id = 1,
                    identity = "identity",
                    isIdentityUserEditable = "N",
                    isSsidHidden = "N",
                    networkType = "networkType",
                    passphrase = "passphrase",
                    password = "password",
                    phase2Method = "phase2Method",
                    priority = 1,
                    restrictFastLaneQosMarking = "N",
                    sortOrder = 1,
                    ssid = "ssid",
                    titleText = "Title",
                    tlsClientCertificateRequired = "N",
                    tlsClientCertificateSupport = "N",
                    tlsMaximumVersion = "tlsMaximumVersion",
                    tlsMinimumVersion = "tlsMinimumVersion",
                    tlsPreferredVersion = "tlsPreferredVersion",
                ),
            ),
            onBackPressed = {},
            onConnectPressed = {},
            onDisconnectPressed = {},
        )
    }
}
