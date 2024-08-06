package com.advice.wifi

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import com.advice.core.local.wifi.WirelessNetwork
import timber.log.Timber

/**
 * Enable a network using the old API.
 *
 * This is used on Android 9 and below.
 */
fun enableNetwork(
    wifiManager: WifiManager,
    wirelessNetwork: WirelessNetwork,
    enterpriseConfig: WifiEnterpriseConfig,
): ConnectionResult.Success {
    val existing = wifiManager.existingWirelessConfig(wirelessNetwork.ssid)
    if (existing != null) {
        Timber.e("Wifi config already exists for ${wirelessNetwork.ssid}")
    }

    val currentConfig = existing ?: WifiConfiguration()

    // This sets the CA certificate.
    currentConfig.enterpriseConfig = enterpriseConfig

    // todo: ensure these values are all correctly set from the wifi_network configuration.
    // General (old) config settings
    currentConfig.SSID = surroundWithQuotes(wirelessNetwork.ssid)
    currentConfig.hiddenSSID = wirelessNetwork.isSsidHidden.toBoolean()
    currentConfig.priority = wirelessNetwork.priority
    currentConfig.status = WifiConfiguration.Status.ENABLED

    currentConfig.allowedKeyManagement.clear()
    currentConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
    currentConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X)

    // GroupCiphers (Allow most ciphers)
    currentConfig.allowedGroupCiphers.clear()
    currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
    currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
    currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.GCMP_256)

    // PairwiseCiphers (CCMP = WPA2 only)
    currentConfig.allowedPairwiseCiphers.clear()
    currentConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
    currentConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
    currentConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.GCMP_256)

    // Authentication Algorithms (OPEN)
    currentConfig.allowedAuthAlgorithms.clear()
    currentConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)

    // Protocols (RSN/WPA2 only)
    currentConfig.allowedProtocols.clear()
    currentConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
    currentConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)

    val result = if (existing == null) {
        val networkId = wifiManager.addNetwork(currentConfig)
        Timber.e("Network ID: $networkId")
        wifiManager.enableNetwork(networkId, false)
    } else {
        Timber.e("Existing Network ID: ${existing.networkId}")
        wifiManager.updateNetwork(currentConfig)
        wifiManager.enableNetwork(currentConfig.networkId, false)
    }
    wifiManager.saveConfiguration()
    Timber.e("Enable network result: $result")

    return ConnectionResult.Success
}
