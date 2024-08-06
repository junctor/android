package com.advice.wifi

import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.core.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class WirelessConnectionManager(
    private val wifiManager: WifiManager,
) {
    suspend fun addNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
    ): ConnectionResult {
        val certificate = try {
            wirelessNetwork.getCertificate()
        } catch (ex: Exception) {
            val message = "Error in downloading certificate: $ex"
            Timber.e(message)
            return ConnectionResult.Error(message)
        }

        val enterpriseConfig = try {
            wirelessNetwork.toWifiEnterpriseConfig(certificate)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun removeNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
    ): ConnectionResult {
        return try {
            val suggestion =
                WifiNetworkSuggestion.Builder()
                    .setSsid(wirelessNetwork.ssid)
                    .build()

            wifiManager.removeNetworkSuggestions(listOf(suggestion))
            ConnectionResult.Success
        } catch (ex: Exception) {
            val message = "Error in removing wifi config: $ex"
            Timber.e(message)
            ConnectionResult.Error(message)
        }
    }

    /**
     * Downloads the certificate from the given URL and returns it as an X509Certificate.
     */
    private suspend fun WirelessNetwork.getCertificate(): X509Certificate? {
        val certificates = certs
        if (certificates.isNullOrEmpty()) {
            return null
        }

        val it = certificates.first()
        val certFactory = CertificateFactory.getInstance("X.509")
        val inputStream: InputStream = downloadCertificate(it.url)
        return certFactory.generateCertificate(inputStream) as X509Certificate
    }

    private suspend fun downloadCertificate(urlString: String): InputStream =
        withContext(Dispatchers.IO) {
            val client = Network.client
            val request = Request.Builder().url(urlString).build()

            Timber.e("Downloading certificate from $urlString")

            val response = client.newCall(request).execute()

            Timber.e("Response: $response")

            if (!response.isSuccessful) {
                val message = "Unexpected code $response"
                Timber.e(message)
                throw IOException(message)
            }

            response.body.byteStream()
        }
}
