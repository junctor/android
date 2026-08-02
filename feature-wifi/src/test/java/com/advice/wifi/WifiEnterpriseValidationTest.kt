package com.advice.wifi

import android.net.wifi.WifiEnterpriseConfig
import com.advice.core.local.wifi.EapSubject
import com.advice.core.local.wifi.WirelessNetwork
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.security.cert.X509Certificate

class WifiEnterpriseValidationTest {
    @Test
    fun `open network is rejected`() {
        val network = wirelessNetwork(networkType = "OPEN", eapMethod = null)

        assertTrue(network.isOpenOrPersonalNetwork())
        assertNotNull(network.validateEnterpriseOnly())
    }

    @Test
    fun `psk network is rejected`() {
        val network = wirelessNetwork(networkType = "WPA2-PSK", eapMethod = null, passphrase = "secret")

        assertTrue(network.isOpenOrPersonalNetwork())
        assertNotNull(network.validateEnterpriseOnly())
    }

    @Test
    fun `personal network without passphrase is rejected`() {
        val network = wirelessNetwork(networkType = "WPA2-Personal", eapMethod = null, passphrase = null)

        assertTrue(network.isOpenOrPersonalNetwork())
        assertNotNull(network.validateEnterpriseOnly())
    }

    @Test
    fun `enterprise PEAP network is allowed`() {
        val network = wirelessNetwork(networkType = "WPA2-Enterprise", eapMethod = "PEAP", passphrase = null)

        assertFalse(network.isOpenOrPersonalNetwork())
        assertNull(network.validateEnterpriseOnly())
    }

    @Test
    fun `applyServerIdentityMatch uses domainSuffixMatch for all DNS subjects`() {
        val network =
            wirelessNetwork(
                eapSubjects =
                    listOf(
                        EapSubject(type = "DNS", value = "a.example.com"),
                        EapSubject(type = "DNS", value = "b.example.com"),
                    ),
            )
        val config = mockk<WifiEnterpriseConfig>(relaxed = true)

        network.applyServerIdentityMatch(config)

        verify { config.domainSuffixMatch = "a.example.com;b.example.com" }
    }

    @Test
    fun `applyServerIdentityMatch uses altSubjectMatch for mixed subjects`() {
        val network =
            wirelessNetwork(
                eapSubjects =
                    listOf(
                        EapSubject(type = "DNS", value = "a.example.com"),
                        EapSubject(type = "EMAIL", value = "wifi@example.com"),
                    ),
            )
        val config = mockk<WifiEnterpriseConfig>(relaxed = true)

        network.applyServerIdentityMatch(config)

        verify { config.altSubjectMatch = "DNS:a.example.com;EMAIL:wifi@example.com" }
    }

    @Test
    fun `validateForSuggestion fails TLS without CA`() {
        val config = mockk<WifiEnterpriseConfig>()
        every { config.eapMethod } returns WifiEnterpriseConfig.Eap.TLS
        every { config.caCertificate } returns null

        assertEquals(
            "A CA certificate is required for this enterprise network.",
            config.validateForSuggestion(),
        )
    }

    @Test
    fun `validateForSuggestion succeeds with CA and domain`() {
        // Android unit stubs do not implement WifiEnterpriseConfig setters/getters.
        val config = mockk<WifiEnterpriseConfig>()
        every { config.eapMethod } returns WifiEnterpriseConfig.Eap.TLS
        every { config.caCertificate } returns mockk<X509Certificate>(relaxed = true)
        every { config.domainSuffixMatch } returns "example.com"
        every { config.altSubjectMatch } returns null

        assertNull(config.validateForSuggestion())
    }

    private fun wirelessNetwork(
        networkType: String = "WPA2-Enterprise",
        eapMethod: String? = "PEAP",
        passphrase: String? = null,
        eapSubjects: List<EapSubject>? = listOf(EapSubject()),
    ) = WirelessNetwork(
        anonymousIdentity = null,
        autoJoin = "Y",
        certs = null,
        descriptionText = "",
        disableAssociationMacRandomization = "N",
        disableCaptiveNetworkDetection = "N",
        eapMethod = eapMethod,
        eapSubjects = eapSubjects,
        enableIpv6 = "Y",
        id = 1,
        identity = "user",
        isIdentityUserEditable = "Y",
        isSsidHidden = "N",
        networkType = networkType,
        passphrase = passphrase,
        password = "pass",
        phase2Method = "MSCHAPV2",
        priority = 0,
        restrictFastLaneQosMarking = "N",
        sortOrder = 0,
        ssid = "ssid",
        titleText = "Network",
        tlsClientCertificateRequired = null,
        tlsClientCertificateSupport = null,
        tlsMaximumVersion = null,
        tlsMinimumVersion = null,
        tlsPreferredVersion = null,
    )
}
