package com.advice.wifi

import android.content.Context
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

fun loadCertificate(context: Context, resourceId: Int): X509Certificate {
    val certificateFactory = CertificateFactory.getInstance("X.509")

    context.resources.openRawResource(resourceId).use { inputStream ->
        return certificateFactory.generateCertificate(inputStream) as X509Certificate
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun suggestNetwork(context: Context) {
    val ssid = "DefCon"
    // todo: replace with the actual identity and password
    val identity = "YourIdentity"
    val password = "YourPassword"

    // Load the certificate from the resources
    val certificate = loadCertificate(context, R.raw.my_certificate)

    val enterpriseConfig = WifiEnterpriseConfig().apply {
        eapMethod = WifiEnterpriseConfig.Eap.TLS
        phase2Method = WifiEnterpriseConfig.Phase2.NONE
        setIdentity(identity)
        anonymousIdentity = identity
        setPassword(password)
        caCertificate = certificate

        // todo: replace with your server's actual domain
        setAltSubjectMatch("DNS:wifireg.defcon.org")
    }

    val suggestion = WifiNetworkSuggestion.Builder()
        .setSsid(ssid)
        .setWpa2EnterpriseConfig(enterpriseConfig)
        .setIsAppInteractionRequired(true)
        .build()

    val suggestions = listOf(suggestion)

    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val status = wifiManager.addNetworkSuggestions(suggestions)

    if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
        // Successfully added network suggestion
        Timber.e("Successfully added network suggestion")
    } else {
        // Failed to add network suggestion
        Timber.e("Failed to add network suggestion")
    }
}
