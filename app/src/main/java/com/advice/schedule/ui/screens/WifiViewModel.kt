package com.advice.schedule.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.wifi.Profile
import com.advice.wifi.WifiConnectionManager
import com.advice.wifi.ui.screens.WiFiScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class WifiViewModel : ViewModel(), KoinComponent {

    private val manager by inject<WifiConnectionManager>()

    private val _state = MutableStateFlow(WiFiScreenViewState())
    val state = _state

    fun saveWifiConfig() {
        val state = _state.value
        viewModelScope.launch {
            if (state.profile is Profile.Custom) {
                manager.saveWifiConfig(
                    ssid = state.ssid,
                    username = state.username,
                    password = state.password,
                )
            } else {
                manager.saveWifiConfig(
                    username = state.profile.username,
                    password = state.profile.password,
                )
            }
        }
    }

    fun updateState(state: WiFiScreenViewState) {
        _state.value = state
    }
}
