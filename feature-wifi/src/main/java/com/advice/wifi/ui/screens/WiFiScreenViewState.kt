package com.advice.wifi.ui.screens

import com.advice.wifi.Profile

data class WiFiScreenViewState(
    val ssid: String = "",
    val username: String = "",
    val password: String = "",
    val profile: Profile = Profile.AllowAny,
)
