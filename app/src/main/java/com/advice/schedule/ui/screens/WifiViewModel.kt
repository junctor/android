package com.advice.schedule.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.data.repositories.WifiNetworkRepository
import com.advice.wifi.WirelessConnectionManager
import com.advice.wifi.ui.screens.WiFiScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class WifiViewModel : ViewModel(), KoinComponent {

    private val manager by inject<WirelessConnectionManager>()
    private val repository by inject<WifiNetworkRepository>()

    private val _state = MutableStateFlow<WiFiScreenViewState>(WiFiScreenViewState.Loading)
    val state = _state

    fun get(id: Long) {
        viewModelScope.launch {
            val wifiNetworks = repository.get(id)
            _state.value = if (wifiNetworks == null) {
                WiFiScreenViewState.Error
            } else {
                WiFiScreenViewState.Loaded(wifiNetworks)
            }
        }
    }

    fun saveWifiConfig() {
        val state = _state.value as? WiFiScreenViewState.Loaded ?: return
        viewModelScope.launch {
            val result = manager.addNetworkSuggestion(state.wirelessNetwork)
            _state.value = WiFiScreenViewState.Loaded(state.wirelessNetwork, result)
        }
    }
}
