package com.advice.wifi

import android.content.res.Resources
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import com.advice.core.local.wifi.WirelessNetwork
import timber.log.Timber
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class WirelessConnectionManager(
    private val resources: Resources,
    private val wifiManager: WifiManager,
) {
    fun addNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
    ): ConnectionResult {
        val enterpriseConfig = try {
            val caCert = getLocalCaCertificate()
            wirelessNetwork.toWifiEnterpriseConfig(caCert)
        } catch (ex: Exception) {
            val message = "Error in applying Android 4.3 enterprise settings: $ex"
            Timber.e(message)
            return ConnectionResult.Error(message)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // On Android 10 and above, we use the new API
            addNetworkSuggestion(wirelessNetwork, enterpriseConfig)
        } else {
            // On Android 9 and below, we use the old API
            enableNetwork(wifiManager, wirelessNetwork, enterpriseConfig)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
        enterpriseConfig: WifiEnterpriseConfig
    ): ConnectionResult {
        return try {
            val suggestion =
                WifiNetworkSuggestion.Builder()
                    .setSsid(wirelessNetwork.ssid)
                    .setWpa2EnterpriseConfig(enterpriseConfig).build()

            wifiManager.addNetworkSuggestions(listOf(suggestion))
            ConnectionResult.Success
        } catch (ex: Exception) {
            val message = "Error in saving wifi config: $ex"
            Timber.e(message)
            ConnectionResult.Error(message)
        }
    }

    // todo: Replace this with a real certificate from server.
    private fun getLocalCaCertificate(): X509Certificate {
        val certFactory = CertificateFactory.getInstance("X.509")
        val inputStream: InputStream = resources.openRawResource(R.raw.my_certificate)
        return certFactory.generateCertificate(inputStream) as X509Certificate
    }
}
