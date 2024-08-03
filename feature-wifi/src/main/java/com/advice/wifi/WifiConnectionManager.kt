package com.advice.wifi

import android.annotation.SuppressLint
import android.content.res.Resources
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiEnterpriseConfig.Eap
import android.net.wifi.WifiEnterpriseConfig.Phase2
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import timber.log.Timber
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class WifiConnectionManager(
    private val resources: Resources,
    private val wifiManager: WifiManager,
) {
    companion object {
        // FIXME This should be a configuration setting somehow
        private const val INT_EAP = "eap"
        private const val INT_PHASE2 = "phase2"
        private const val INT_ENGINE = "engine"
        private const val INT_ENGINE_ID = "engine_id"
        private const val INT_CLIENT_CERT = "client_cert"
        private const val INT_CA_CERT = "ca_cert"
        private const val INT_PRIVATE_KEY = "private_key"
        private const val INT_PRIVATE_KEY_ID = "key_id"
        private const val INT_SUBJECT_MATCH = "subject_match"
        private const val INT_ALTSUBJECT_MATCH = "altsubject_match"
        private const val INT_PASSWORD = "password"
        private const val INT_IDENTITY = "identity"
        private const val INT_ANONYMOUS_IDENTITY = "anonymous_identity"
        private const val INT_ENTERPRISEFIELD_NAME =
            "android.net.wifi.WifiConfiguration\$EnterpriseField"

        // Because android.security.Credentials cannot be resolved...
        private const val INT_KEYSTORE_URI = "keystore://"
        private const val INT_CA_PREFIX = INT_KEYSTORE_URI + "CACERT_"
        private const val INT_PRIVATE_KEY_PREFIX = INT_KEYSTORE_URI + "USRPKEY_"
        private const val INT_PRIVATE_KEY_ID_PREFIX = "USRPKEY_"
        private const val INT_CLIENT_CERT_PREFIX = INT_KEYSTORE_URI + "USRCERT_"

        private const val DEFAULT_SSID = "37C3"
    }

    fun removeQuotes(str: String): String {
        val len = str.length
        if ((len > 1) && (str[0] == '"') && (str[len - 1] == '"')) {
            return str.substring(1, len - 1)
        }
        return str
    }

    private fun surroundWithQuotes(string: String): String {
        return "\"" + string + "\""
    }

    fun saveWifiConfig(
        ssid: String = DEFAULT_SSID,
        username: String,
        password: String,
    ) {
        val subjectMatch = "/CN=radius.c3noc.net"
        val altSubjectMatch = "DNS:radius.c3noc.net"

        val realm = if (username.contains("@")) {
            username.substring(username.indexOf("@"))
        } else {
            ""
        }

        storeWifiProfile(
            ssid = ssid,
            subjectMatch = subjectMatch,
            altSubjectMatch = altSubjectMatch,
            username = username,
            password = password,
            realm = realm
        )
    }

    @SuppressLint("MissingPermission")
    fun storeWifiProfile(
        ssid: String,
        subjectMatch: String,
        altSubjectMatch: String,
        username: String,
        password: String,
        realm: String,
    ) {
        // Enterprise Settings
        val configMap = HashMap<String, String>()
        configMap[INT_SUBJECT_MATCH] = subjectMatch
        configMap[INT_ALTSUBJECT_MATCH] = altSubjectMatch
        configMap[INT_ANONYMOUS_IDENTITY] = "anonymous$realm"
        configMap[INT_IDENTITY] = username
        configMap[INT_PASSWORD] = password
        configMap[INT_EAP] = "TTLS"
        configMap[INT_PHASE2] = "auth=PAP"
        configMap[INT_ENGINE] = "0"

        var currentConfig = WifiConfiguration()

        val enterpriseConfig = applyAndroid43EnterpriseSettings(configMap)
        if (enterpriseConfig == null) {
            Timber.e("Enterprise config is null")
            return
        }
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2EnterpriseConfig(enterpriseConfig).build()
                wifiManager.addNetworkSuggestions(listOf(suggestion))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        wifiManager.setWifiEnabled(true)

        var configs: List<WifiConfiguration>? = null
        var i = 0
        while (i < 10 && configs == null) {
            configs = wifiManager.configuredNetworks
            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
                // Do nothing ;-)
            }
            i++
        }
        // Use the existing ssid profile if it exists.
        var ssidExists = false
        if (configs != null) {
            for (config in configs) {
                if (config.SSID == surroundWithQuotes(ssid!!)) {
                    currentConfig = config
                    ssidExists = true
                    break
                }
            }
        }
        // This sets the CA certificate.
        currentConfig.enterpriseConfig = enterpriseConfig

        // General (old) config settings
        currentConfig.SSID = surroundWithQuotes(ssid!!)
        currentConfig.hiddenSSID = false
        currentConfig.priority = 40
        currentConfig.status = WifiConfiguration.Status.DISABLED

        currentConfig.allowedKeyManagement.clear()
        currentConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)

        // GroupCiphers (Allow most ciphers)
        currentConfig.allowedGroupCiphers.clear()
        currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        currentConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)


        // PairwiseCiphers (CCMP = WPA2 only)
        currentConfig.allowedPairwiseCiphers.clear()
        currentConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)

        // Authentication Algorithms (OPEN)
        currentConfig.allowedAuthAlgorithms.clear()
        currentConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)

        // Protocols (RSN/WPA2 only)
        currentConfig.allowedProtocols.clear()
        currentConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)

        if (!ssidExists) {
            val networkId = wifiManager.addNetwork(currentConfig)
            wifiManager.enableNetwork(networkId, false)
        } else {
            wifiManager.updateNetwork(currentConfig)
            wifiManager.enableNetwork(currentConfig.networkId, false)
        }
        wifiManager.saveConfiguration()
    }

    private fun applyAndroid43EnterpriseSettings(
        configMap: HashMap<String, String>,
    ): WifiEnterpriseConfig? {
        try {
            val certFactory = CertificateFactory.getInstance("X.509")
            val `in`: InputStream = resources.openRawResource(R.raw.my_certificate)
            // InputStream in = new ByteArrayInputStream(Base64.decode(ca.replaceAll("-----(BEGIN|END) CERTIFICATE-----", ""), 0));
            val caCert: X509Certificate = certFactory.generateCertificate(`in`) as X509Certificate

            val enterpriseConfig = WifiEnterpriseConfig()
            enterpriseConfig.phase2Method = Phase2.PAP
            enterpriseConfig.anonymousIdentity = configMap[INT_ANONYMOUS_IDENTITY]
            enterpriseConfig.eapMethod = Eap.TTLS

            enterpriseConfig.caCertificate = caCert
            enterpriseConfig.identity = configMap[INT_IDENTITY]
            enterpriseConfig.password = configMap[INT_PASSWORD]
            enterpriseConfig.subjectMatch = configMap[INT_SUBJECT_MATCH]
            enterpriseConfig.altSubjectMatch = configMap[INT_ALTSUBJECT_MATCH]

            return enterpriseConfig
        } catch (ex: Exception) {
            Timber.e("Error in applying Android 4.3 enterprise settings: $ex")
            return null
        }
    }
}
