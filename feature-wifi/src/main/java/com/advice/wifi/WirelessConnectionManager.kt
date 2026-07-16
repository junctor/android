package com.advice.wifi

import android.content.Intent
import android.content.res.Resources
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.core.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

sealed class JoinPreparation {
    /** API 30+: launch the system add-networks dialog with this intent. */
    data class LaunchAddNetworks(val intent: Intent) : JoinPreparation()

    /** Join finished inline (API 29 suggestions or legacy enableNetwork). */
    data class Completed(val result: ConnectionResult) : JoinPreparation()
}

class WirelessConnectionManager(
    private val resources: Resources,
    private val wifiManager: WifiManager,
) {
    /**
     * Prepares a Wi-Fi join for the current platform:
     * - API 30+: returns an [Settings.ACTION_WIFI_ADD_NETWORKS] intent
     * - API 29: calls [WifiManager.addNetworkSuggestions]
     * - API 28 and below: uses the legacy [enableNetwork] path
     */
    suspend fun prepareJoin(
        wirelessNetwork: WirelessNetwork,
        forceLocalCert: Boolean = false,
    ): JoinPreparation {
        wirelessNetwork.validateEnterpriseOnly()?.let { error ->
            Timber.e(error)
            return JoinPreparation.Completed(ConnectionResult.Error(error))
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return JoinPreparation.Completed(
                joinLegacy(wirelessNetwork, forceLocalCert)
            )
        }

        val suggestion = buildNetworkSuggestion(wirelessNetwork, forceLocalCert).getOrElse { error ->
            return JoinPreparation.Completed(
                ConnectionResult.Error(error.message ?: "Unable to build Wi-Fi configuration")
            )
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            JoinPreparation.LaunchAddNetworks(createAddNetworksIntent(suggestion))
        } else {
            JoinPreparation.Completed(addNetworkSuggestion(suggestion))
        }
    }

    private suspend fun joinLegacy(
        wirelessNetwork: WirelessNetwork,
        forceLocalCert: Boolean,
    ): ConnectionResult {
        wirelessNetwork.validateEnterpriseOnly()?.let { error ->
            Timber.e(error)
            return ConnectionResult.Error(error)
        }

        val certificate = try {
            if (forceLocalCert) {
                wirelessNetwork.getLocalCertificate()
            } else {
                wirelessNetwork.getCertificate()
            }
        } catch (ex: Exception) {
            val message = "Error loading certificate: ${ex.message}"
            Timber.e(ex, message)
            return ConnectionResult.Error(message)
        }

        val enterpriseConfig = try {
            wirelessNetwork.toWifiEnterpriseConfig(certificate)
        } catch (ex: Exception) {
            val message = "Error applying enterprise settings: ${ex.message}"
            Timber.e(ex, message)
            return ConnectionResult.Error(message)
        }

        return enableNetwork(wifiManager, wirelessNetwork, enterpriseConfig)
    }

    /**
     * Builds a WPA2/WPA3-Enterprise [WifiNetworkSuggestion] only.
     * Open and WPA Personal networks are rejected.
     */
    suspend fun buildNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
        forceLocalCert: Boolean = false,
    ): Result<WifiNetworkSuggestion> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Result.failure(IllegalStateException("WifiNetworkSuggestion requires API 29+"))
        }

        wirelessNetwork.validateEnterpriseOnly()?.let { error ->
            Timber.e(error)
            return Result.failure(IllegalStateException(error))
        }

        val certificate = try {
            if (forceLocalCert) {
                wirelessNetwork.getLocalCertificate()
            } else {
                wirelessNetwork.getCertificate()
            }
        } catch (ex: Exception) {
            val message = "Error loading certificate: ${ex.message}"
            Timber.e(ex, message)
            return Result.failure(IllegalStateException(message))
        }

        val enterpriseConfig = try {
            wirelessNetwork.toWifiEnterpriseConfig(certificate)
        } catch (ex: Exception) {
            val message = "Error applying enterprise settings: ${ex.message}"
            Timber.e(ex, message)
            return Result.failure(IllegalStateException(message))
        }

        enterpriseConfig.validateForSuggestion()?.let { error ->
            Timber.e(error)
            return Result.failure(IllegalStateException(error))
        }

        return try {
            Result.success(buildEnterpriseSuggestion(wirelessNetwork, enterpriseConfig))
        } catch (ex: IllegalArgumentException) {
            val message = "Enterprise configuration rejected: ${ex.message}"
            Timber.e(ex, message)
            Result.failure(IllegalStateException(message))
        } catch (ex: Exception) {
            val message = "Error building Wi-Fi suggestion: ${ex.message}"
            Timber.e(ex, message)
            Result.failure(IllegalStateException(message))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addNetworkSuggestion(suggestion: WifiNetworkSuggestion): ConnectionResult {
        return try {
            val result = wifiManager.addNetworkSuggestions(listOf(suggestion))
            Timber.d("Network suggestion added: $result")
            suggestionStatusResult(result, removing = false)
        } catch (ex: Exception) {
            val message = "Error saving Wi-Fi suggestion: ${ex.message}"
            Timber.e(ex, message)
            ConnectionResult.Error(message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun createAddNetworksIntent(suggestion: WifiNetworkSuggestion): Intent {
        return Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
            putParcelableArrayListExtra(
                Settings.EXTRA_WIFI_NETWORK_LIST,
                arrayListOf(suggestion),
            )
        }
    }

    private fun suggestionStatusResult(result: Int, removing: Boolean): ConnectionResult {
        return if (result == STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            if (removing) ConnectionResult.Removed else ConnectionResult.Suggested
        } else {
            val action = if (removing) "removing" else "adding"
            ConnectionResult.Error("Error in $action network suggestion ($result)")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun removeNetworkSuggestion(
        wirelessNetwork: WirelessNetwork,
        forceLocalCert: Boolean = false,
    ): ConnectionResult {
        val suggestion = buildNetworkSuggestion(wirelessNetwork, forceLocalCert).getOrElse { error ->
            return ConnectionResult.Error(error.message ?: "Unable to build Wi-Fi configuration")
        }

        return try {
            val result = wifiManager.removeNetworkSuggestions(listOf(suggestion))
            suggestionStatusResult(result, removing = true)
        } catch (ex: Exception) {
            val message = "Error removing Wi-Fi suggestion: ${ex.message}"
            Timber.e(ex, message)
            ConnectionResult.Error(message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buildEnterpriseSuggestion(
        wirelessNetwork: WirelessNetwork,
        enterpriseConfig: WifiEnterpriseConfig,
    ): WifiNetworkSuggestion {
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(wirelessNetwork.ssid)
            .setIsHiddenSsid(wirelessNetwork.isSsidHidden.toBooleanFlag())

        applyMacRandomization(builder, wirelessNetwork)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && wirelessNetwork.isWpa3Enterprise()) {
            builder.setWpa3EnterpriseStandardModeConfig(enterpriseConfig)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && wirelessNetwork.isWpa3Enterprise()) {
            @Suppress("DEPRECATION")
            builder.setWpa3EnterpriseConfig(enterpriseConfig)
        } else {
            builder.setWpa2EnterpriseConfig(enterpriseConfig)
        }

        return builder.build().also {
            Timber.d("Network suggestion: $it")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun applyMacRandomization(
        builder: WifiNetworkSuggestion.Builder,
        wirelessNetwork: WirelessNetwork,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        // Suggestion API only supports PERSISTENT / NON_PERSISTENT — not factory MAC (NONE).
        // When the network asks to disable randomization, leave the platform default.
        if (!wirelessNetwork.disableAssociationMacRandomization.toBooleanFlag()) {
            builder.setMacRandomizationSetting(WifiNetworkSuggestion.RANDOMIZATION_PERSISTENT)
        }
    }

    private suspend fun WirelessNetwork.getLocalCertificate(): X509Certificate? =
        withContext(Dispatchers.IO) {
            val certificates = certs
            if (certificates.isNullOrEmpty()) {
                return@withContext null
            }

            val certFactory = CertificateFactory.getInstance("X.509")
            val inputStream: InputStream =
                resources.openRawResource(R.raw.defcon32_harica_inter_root)
            val availableBytes = inputStream.available()
            Timber.d("Available bytes in the input stream: $availableBytes")

            val certificateCollection = certFactory.generateCertificates(inputStream)

            Timber.d("Certificate collection: ${certificateCollection.size}")
            for (certificate in certificateCollection) {
                Timber.d("Certificate: $certificate")
            }

            return@withContext certificateCollection.first() as X509Certificate
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
        return downloadCertificate(it.url).use { inputStream ->
            certFactory.generateCertificate(inputStream) as X509Certificate
        }
    }

    private suspend fun downloadCertificate(urlString: String): InputStream =
        withContext(Dispatchers.IO) {
            val client = Network.client
            val request = Request.Builder().url(urlString).build()

            Timber.d("Downloading certificate from $urlString")

            client.newCall(request).execute().use { response ->
                Timber.d("Response: $response")

                if (!response.isSuccessful) {
                    val message = "Unexpected code $response"
                    Timber.e(message)
                    throw IOException(message)
                }

                // Buffer the body so the response (and connection) can be closed before returning.
                ByteArrayInputStream(response.body.bytes())
            }
        }
}
