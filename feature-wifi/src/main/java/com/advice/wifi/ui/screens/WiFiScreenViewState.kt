package com.advice.wifi.ui.screens

import com.advice.core.local.wifi.WirelessNetwork
import com.advice.wifi.ConnectionResult


sealed class WiFiScreenViewState {
    data object Loading : WiFiScreenViewState()
    data class Loaded(
        val wirelessNetwork: WirelessNetwork,
        val forceLocalCert: Boolean = false,
        val result: ConnectionResult? = null,
    ) : WiFiScreenViewState()

    data object Error : WiFiScreenViewState()
}
