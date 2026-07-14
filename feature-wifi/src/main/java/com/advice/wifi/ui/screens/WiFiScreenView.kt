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
import com.advice.ui.components.SwitchPreference
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(
    state: WiFiScreenViewState,
    onBackPressed: () -> Unit,
    onConnectPressed: () -> Unit,
    onDisconnectPressed: () -> Unit,
    onForceLocalCert: (Boolean) -> Unit,
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
            onForceLocalCert = onForceLocalCert,
        )
    }
}

@Composable
fun WifiScreenContent(
    state: WiFiScreenViewState,
    modifier: Modifier,
    onConnectPressed: () -> Unit,
    onDisconnectPressed: () -> Unit,
    onForceLocalCert: (Boolean) -> Unit,
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
                SwitchPreference(
                    title = "Force Local Cert",
                    isChecked = state.forceLocalCert,
                    summary = "defcon32_harica_inter_root.cer",
                ) {
                    onForceLocalCert(it)
                }

                Column {
                    Text("Status:")
                    Text(state.result?.displayMessage() ?: "Not saved yet")
                }

                Text(
                    "Saving credentials does not connect immediately. " +
                        "Android connects when this network is in range."
                )

                Button(
                    onClick = { onConnectPressed() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = roundedCornerShape,
                ) {
                    Text("Save Wi-Fi credentials")
                }

                Button(
                    onClick = { onDisconnectPressed() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = roundedCornerShape,
                ) {
                    Text("Remove Wi-Fi credentials")
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
                    eapMethod = "PEAP",
                    eapSubjects = emptyList(),
                    enableIpv6 = "N",
                    id = 1,
                    identity = "identity",
                    isIdentityUserEditable = "N",
                    isSsidHidden = "N",
                    networkType = "WPA2-Enterprise",
                    passphrase = null,
                    password = "password",
                    phase2Method = "MSCHAPV2",
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
            onForceLocalCert = {},
        )
    }
}
