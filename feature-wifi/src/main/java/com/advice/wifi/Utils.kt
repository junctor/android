package com.advice.wifi

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import com.advice.core.local.wifi.WirelessNetwork
import timber.log.Timber
import java.security.cert.X509Certificate
import java.util.Locale

fun WifiManager.existingWirelessConfig(ssid: String): WifiConfiguration? {
    setWifiEnabled(true)

    val configs = configuredNetworks
    val existing = configs.firstOrNull { it.SSID == surroundWithQuotes(ssid) }
    return existing
}

fun WirelessNetwork.toWifiEnterpriseConfig(
    caCertificate: X509Certificate,
): WifiEnterpriseConfig {
    val enterpriseConfig = WifiEnterpriseConfig()
    if (phase2Method != null) {
        enterpriseConfig.phase2Method = phase2Method.toPhase2Method()
    }
    enterpriseConfig.anonymousIdentity = anonymousIdentity
    if (eapMethod != null) {
        enterpriseConfig.eapMethod = eapMethod.convertEapMethod()
    }

    enterpriseConfig.caCertificate = caCertificate
    enterpriseConfig.identity = identity
    enterpriseConfig.password = password
    // Example: "/CN=radius.c3noc.net"
    enterpriseConfig.subjectMatch = toSubjectMatch()
    // Example: "DNS:radius.c3noc.net"
    enterpriseConfig.altSubjectMatch = toSubjectMatch()
    return enterpriseConfig
}

fun String.toBoolean(): Boolean {
    return this == "Y"
}

private fun String?.convertEapMethod(): Int {
    return when (this?.toUpperCase(Locale.ROOT)) {
        "PEAP" -> WifiEnterpriseConfig.Eap.PEAP
        "TLS" -> WifiEnterpriseConfig.Eap.TLS
        "TTLS" -> WifiEnterpriseConfig.Eap.TTLS
        "PWD" -> WifiEnterpriseConfig.Eap.PWD
        "SIM" -> WifiEnterpriseConfig.Eap.SIM
        "AKA" -> WifiEnterpriseConfig.Eap.AKA
        "AKA_PRIME" -> WifiEnterpriseConfig.Eap.AKA_PRIME
        else -> WifiEnterpriseConfig.Eap.NONE
    }
}

private fun String?.toPhase2Method(): Int {
    return when (this?.toUpperCase(Locale.ROOT)) {
        "PAP" -> WifiEnterpriseConfig.Phase2.PAP
        "MSCHAP" -> WifiEnterpriseConfig.Phase2.MSCHAP
        "MSCHAPV2" -> WifiEnterpriseConfig.Phase2.MSCHAPV2
        "GTC" -> WifiEnterpriseConfig.Phase2.GTC
        "SIM" -> WifiEnterpriseConfig.Phase2.SIM
        "AKA" -> WifiEnterpriseConfig.Phase2.AKA
        "AKA_PRIME" -> WifiEnterpriseConfig.Phase2.AKA_PRIME
        else -> WifiEnterpriseConfig.Phase2.NONE
    }
}

internal fun WirelessNetwork.toSubjectMatch(): String? {
    return eapSubjects?.firstOrNull()?.value
}

fun surroundWithQuotes(string: String): String {
    return "\"" + string + "\""
}
