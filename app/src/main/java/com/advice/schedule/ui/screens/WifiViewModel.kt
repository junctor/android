package com.advice.schedule.ui.screens

import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.data.repositories.WifiNetworkRepository
import com.advice.wifi.ConnectionResult
import com.advice.wifi.JoinPreparation
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

    /**
     * Prepares a join. When the platform needs the system add-networks dialog, [onLaunchAddNetworks]
     * is invoked with the intent; otherwise the result is written into screen state.
     */
    fun saveWifiConfig(onLaunchAddNetworks: (Intent) -> Unit) {
        val state = _state.value as? WiFiScreenViewState.Loaded ?: return
        viewModelScope.launch {
            when (val preparation = manager.prepareJoin(state.wirelessNetwork, state.forceLocalCert)) {
                is JoinPreparation.LaunchAddNetworks -> onLaunchAddNetworks(preparation.intent)
                is JoinPreparation.Completed -> {
                    _state.value = state.copy(result = preparation.result)
                }
            }
        }
    }

    fun onAddNetworksResult(resultCode: Int) {
        val state = _state.value as? WiFiScreenViewState.Loaded ?: return
        val result = when (resultCode) {
            android.app.Activity.RESULT_OK -> ConnectionResult.SavedViaSettings
            android.app.Activity.RESULT_CANCELED -> ConnectionResult.Cancelled
            else -> ConnectionResult.Error("Unexpected result from Wi-Fi settings ($resultCode)")
        }
        _state.value = state.copy(result = result)
    }

    fun disconnect() {
        val state = _state.value as? WiFiScreenViewState.Loaded ?: return
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val result = manager.removeNetworkSuggestion(
                    state.wirelessNetwork,
                    state.forceLocalCert,
                )
                _state.value = state.copy(result = result)
            } else {
                _state.value = state.copy(
                    result = ConnectionResult.Error(
                        "Removing saved networks is not supported on this Android version."
                    )
                )
            }
        }
    }

    fun forceLocalCert(force: Boolean) {
        val state = _state.value as? WiFiScreenViewState.Loaded ?: return
        _state.value = state.copy(forceLocalCert = force)
    }
}
