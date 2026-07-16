package com.advice.wifi

sealed class ConnectionResult {
    /** Credentials accepted via [android.net.wifi.WifiManager.addNetworkSuggestions] (API 29). */
    data object Suggested : ConnectionResult()

    /** User saved the network through the system [android.provider.Settings.ACTION_WIFI_ADD_NETWORKS] dialog. */
    data object SavedViaSettings : ConnectionResult()

    /** User cancelled the system add-networks dialog. */
    data object Cancelled : ConnectionResult()

    /** Network suggestion removed successfully. */
    data object Removed : ConnectionResult()

    data class Error(val message: String) : ConnectionResult()

    fun displayMessage(): String = when (this) {
        Suggested ->
            "Network credentials saved. Android will connect when this network is in range."
        SavedViaSettings ->
            "Network saved. Android will connect when this network is in range."
        Cancelled ->
            "Save cancelled. No network credentials were added."
        Removed ->
            "Network credentials removed."
        is Error -> message
    }
}
