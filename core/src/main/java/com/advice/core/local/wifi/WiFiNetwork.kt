package com.advice.core.local.wifi

data class WiFiNetwork(
    val anonymousIdentity: String?,
    val autoJoin: String,
    val certs: List<WifiCertificate>?,
    val descriptionText: String,
    val disableAssociationMacRandomization: String,
    val disableCaptiveNetworkDetection: String,
    val eapMethod: String?,
    val eapSubjects: List<EapSubject>?,
    val enableIpv6: String,
    val id: Long,
    val identity: String?,
    val isIdentityUserEditable: String?,
    val isSsidHidden: String,
    val networkType: String,
    val passphrase: String?,
    val password: String?,
    val phase2Method: String?,
    val priority: Int,
    val restrictFastLaneQosMarking: String,
    val sortOrder: Int,
    val ssid: String,
    val titleText: String,
    val tlsClientCertificateRequired: String?,
    val tlsClientCertificateSupport: String?,
    val tlsMaximumVersion: String?,
    val tlsMinimumVersion: String?,
    val tlsPreferredVersion: String?
)
