package com.advice.firebase.models.wifi

import com.advice.core.local.wifi.EapSubject
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.core.local.wifi.WifiCertificate
import com.google.firebase.firestore.PropertyName

data class FirebaseWiFiNetwork(
    @get:PropertyName("anonymousidentity")
    @set:PropertyName("anonymousidentity")
    var anonymousIdentity: String? = null,
    @get:PropertyName("apple_mobileconfig_url")
    @set:PropertyName("apple_mobileconfig_url")
    var appleMobileconfigUrl: String? = null,
    @get:PropertyName("auto_join")
    @set:PropertyName("auto_join")
    var autoJoin: String = "N",
    @get:PropertyName("certs")
    @set:PropertyName("certs")
    var certs: List<Long>? = null,
    @get:PropertyName("description_text")
    @set:PropertyName("description_text")
    var descriptionText: String = "",
    @get:PropertyName("disable_association_mac_randomization")
    @set:PropertyName("disable_association_mac_randomization")
    var disableAssociationMacRandomization: String = "N",
    @get:PropertyName("disable_captive_network_detection")
    @set:PropertyName("disable_captive_network_detection")
    var disableCaptiveNetworkDetection: String= "N",
    @get:PropertyName("eap_method")
    @set:PropertyName("eap_method")
    var eapMethod: String? = null,
    @get:PropertyName("eap_subjects")
    @set:PropertyName("eap_subjects")
    var eapSubjects: List<EapSubject>? = null,
    @get:PropertyName("enable_ipv6")
    @set:PropertyName("enable_ipv6")
    var enableIpv6: String = "N",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("identity")
    @set:PropertyName("identity")
    var identity: String? = null,
    @get:PropertyName("is_identity_user_editable")
    @set:PropertyName("is_identity_user_editable")
    var isIdentityUserEditable: String? = null,
    @get:PropertyName("is_ssid_hidden")
    @set:PropertyName("is_ssid_hidden")
    var isSsidHidden: String = "N",
    @get:PropertyName("network_type")
    @set:PropertyName("network_type")
    var networkType: String = "WPA2-Open",
    @get:PropertyName("passphrase")
    @set:PropertyName("passphrase")
    var passphrase: String? = null,
    @get:PropertyName("password")
    @set:PropertyName("password")
    var password: String? = null,
    @get:PropertyName("phase2_method")
    @set:PropertyName("phase2_method")
    var phase2Method: String? = null,
    @get:PropertyName("priority")
    @set:PropertyName("priority")
    var priority: Int = 0,
    @get:PropertyName("restrict_fast_lane_qos_marking")
    @set:PropertyName("restrict_fast_lane_qos_marking")
    var restrictFastLaneQosMarking: String = "N",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("ssid")
    @set:PropertyName("ssid")
    var ssid: String = "",
    @get:PropertyName("title_text")
    @set:PropertyName("title_text")
    var titleText: String = "",
    @get:PropertyName("tls_client_certificate_required")
    @set:PropertyName("tls_client_certificate_required")
    var tlsClientCertificateRequired: String? = null,
    @get:PropertyName("tls_client_certificate_support")
    @set:PropertyName("tls_client_certificate_support")
    var tlsClientCertificateSupport: String? = null,
    @get:PropertyName("tls_maximum_version")
    @set:PropertyName("tls_maximum_version")
    var tlsMaximumVersion: String? = null,
    @get:PropertyName("tls_minimum_version")
    @set:PropertyName("tls_minimum_version")
    var tlsMinimumVersion: String? = null,
    @get:PropertyName("tls_preferred_version")
    @set:PropertyName("tls_preferred_version")
    var tlsPreferredVersion: String? = null,
)

fun FirebaseWiFiNetwork.toWiFiNetwork(certificates: List<WifiCertificate>?): WirelessNetwork {
    return WirelessNetwork(
        anonymousIdentity = this.anonymousIdentity,
        autoJoin = this.autoJoin,
        certs = certificates,
        descriptionText = this.descriptionText,
        disableAssociationMacRandomization = this.disableAssociationMacRandomization,
        disableCaptiveNetworkDetection = this.disableCaptiveNetworkDetection,
        eapMethod = this.eapMethod,
        eapSubjects = this.eapSubjects,
        enableIpv6 = this.enableIpv6,
        id = this.id,
        identity = this.identity,
        isIdentityUserEditable = this.isIdentityUserEditable,
        isSsidHidden = this.isSsidHidden,
        networkType = this.networkType,
        passphrase = this.passphrase,
        password = this.password,
        phase2Method = this.phase2Method,
        priority = this.priority,
        restrictFastLaneQosMarking = this.restrictFastLaneQosMarking,
        sortOrder = this.sortOrder,
        ssid = this.ssid,
        titleText = this.titleText,
        tlsClientCertificateRequired = this.tlsClientCertificateRequired,
        tlsClientCertificateSupport = this.tlsClientCertificateSupport,
        tlsMaximumVersion = this.tlsMaximumVersion,
        tlsMinimumVersion = this.tlsMinimumVersion,
        tlsPreferredVersion = this.tlsPreferredVersion
    )
}
