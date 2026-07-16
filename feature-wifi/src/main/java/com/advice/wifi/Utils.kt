package com.advice.wifi

import android.Manifest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import androidx.annotation.RequiresPermission
import com.advice.core.local.wifi.WirelessNetwork
import java.security.cert.X509Certificate
import java.util.Locale

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
fun WifiManager.existingWirelessConfig(ssid: String): WifiConfiguration? {
    isWifiEnabled = true

    val configs = configuredNetworks
    val existing = configs?.firstOrNull { it.SSID == surroundWithQuotes(ssid) }
    return existing
}

fun WirelessNetwork.toWifiEnterpriseConfig(
    caCertificate: X509Certificate?,
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
    applyServerIdentityMatch(enterpriseConfig)
    return enterpriseConfig
}

/**
 * Prefer [WifiEnterpriseConfig.setDomainSuffixMatch] for DNS-style subjects; otherwise use
 * [WifiEnterpriseConfig.setAltSubjectMatch] with `TYPE:value` entries.
 */
internal fun WirelessNetwork.applyServerIdentityMatch(enterpriseConfig: WifiEnterpriseConfig) {
    val subjects = eapSubjects
    if (subjects.isNullOrEmpty()) {
        return
    }

    val allDns = subjects.all { it.type.equals("DNS", ignoreCase = true) }
    if (allDns) {
        // Domain suffix match uses bare domain names, not "DNS:value".
        enterpriseConfig.domainSuffixMatch = subjects.joinToString(";") { it.value }
    } else {
        enterpriseConfig.altSubjectMatch = subjects.joinToString(separator = ";") {
            "${it.type}:${it.value}"
        }
    }
}

/**
 * Android 11+ rejects TLS-based enterprise suggestions without a CA cert and server identity match.
 * Returns an error message if the config would be rejected, or null if it looks secure enough.
 */
fun WifiEnterpriseConfig.validateForSuggestion(): String? {
    if (!usesTlsBasedEap()) {
        return null
    }
    if (caCertificate == null) {
        return "A CA certificate is required for this enterprise network."
    }
    val hasDomain = !domainSuffixMatch.isNullOrBlank()
    val hasAltSubject = !altSubjectMatch.isNullOrBlank()
    if (!hasDomain && !hasAltSubject) {
        return "A server identity match (domain or subject) is required for this enterprise network."
    }
    return null
}

private fun WifiEnterpriseConfig.usesTlsBasedEap(): Boolean {
    return eapMethod == WifiEnterpriseConfig.Eap.PEAP ||
        eapMethod == WifiEnterpriseConfig.Eap.TLS ||
        eapMethod == WifiEnterpriseConfig.Eap.TTLS
}

fun String.toBooleanFlag(): Boolean {
    return this == "Y"
}

private fun String?.convertEapMethod(): Int {
    return when (this?.uppercase(Locale.ROOT)) {
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
    return when (this?.uppercase(Locale.ROOT)) {
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

internal fun WirelessNetwork.isWpa3Enterprise(): Boolean {
    val type = networkType.uppercase(Locale.ROOT)
    return type.contains("WPA3") && type.contains("ENTERPRISE")
}

/**
 * Conference Wi-Fi must be WPA2/WPA3-Enterprise only.
 * Returns an error message if this network is open, personal/PSK, or otherwise non-enterprise.
 */
internal fun WirelessNetwork.validateEnterpriseOnly(): String? {
    if (isOpenOrPersonalNetwork()) {
        return "Open and WPA Personal networks are not supported. " +
            "Only WPA2/WPA3-Enterprise networks can be saved."
    }
    val type = networkType.uppercase(Locale.ROOT)
    val looksEnterprise = type.contains("ENTERPRISE") ||
        type.contains("EAP") ||
        !eapMethod.isNullOrBlank()
    if (!looksEnterprise) {
        return "Only WPA2/WPA3-Enterprise networks are supported."
    }
    return null
}

/**
 * True for open or WPA Personal (PSK) networks that must never be suggested or connected.
 */
internal fun WirelessNetwork.isOpenOrPersonalNetwork(): Boolean {
    if (!passphrase.isNullOrBlank()) {
        return true
    }
    val type = networkType.uppercase(Locale.ROOT)
    if (type.contains("PSK") || type.contains("PERSONAL")) {
        return true
    }
    if (type == "OPEN" || type == "NONE" || type == "WPA2-OPEN") {
        return true
    }
    if (type.contains("OPEN") && !type.contains("ENTERPRISE")) {
        return true
    }
    // Bare WPA/WPA2/WPA3 without Enterprise/EAP is treated as personal.
    if ((type == "WPA" || type == "WPA2" || type == "WPA3") && eapMethod.isNullOrBlank()) {
        return true
    }
    return false
}

fun surroundWithQuotes(string: String): String {
    return "\"" + string + "\""
}
