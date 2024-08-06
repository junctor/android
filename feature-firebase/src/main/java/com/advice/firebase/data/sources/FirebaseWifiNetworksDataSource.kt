package com.advice.firebase.data.sources

import com.advice.core.local.wifi.WifiCertificate
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.data.session.UserSession
import com.advice.data.sources.WiFiNetworksDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.wifi.FirebaseWiFiNetwork
import com.advice.firebase.models.wifi.FirebaseWifiCertificate
import com.advice.firebase.models.wifi.toWiFiNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseWifiNetworksDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : WiFiNetworksDataSource {

    private val wifiNetworks = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences/${conference.code}/wifi_networks")
            .snapshotFlowLegacy()
            .closeOnConferenceChange(userSession.getConference())
            .map {
                it.toObjectsOrEmpty(FirebaseWiFiNetwork::class.java).map {
                    val certificates = it.certs?.mapNotNull { id ->
                        getCertificate(id)
                    }
                    it.toWiFiNetwork(certificates)
                }
            }
            .onStart { emit(emptyList()) }
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    private suspend fun getCertificate(id: Long?): WifiCertificate? {
        if (id == null) return null
        val conference = userSession.currentConference ?: return null

        val snapshot = try {
            firestore.document("conferences/${conference.code}/networking_certificates/$id")
                .get()
                .await()
        } catch (ex: Exception) {
            Timber.e(ex, "Error getting certificate")
            return null
        }

        return snapshot.toObjectOrNull(FirebaseWifiCertificate::class.java)?.toWiFiNetwork()
    }

    override fun get(): Flow<List<WirelessNetwork>> {
        return wifiNetworks
    }

    override suspend fun get(id: Long): WirelessNetwork? {
        val conference = userSession.currentConference ?: return null

        val snapshot = try {
            firestore.document("conferences/${conference.code}/wifi_networks/$id")
                .get(Source.CACHE)
                .await()
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to get network with id: $id")
            return null
        }

        val firebase = snapshot.toObjectOrNull(FirebaseWiFiNetwork::class.java) ?: return null
        val certificates = firebase.certs?.mapNotNull { id ->
            getCertificate(id)
        }

        return firebase.toWiFiNetwork(certificates)
    }
}

private fun FirebaseWifiCertificate.toWiFiNetwork(): WifiCertificate? {
    try {
        return WifiCertificate(
            id = id,
            name = nameText,
            url = crtUrl,
        )
    } catch (e: Exception) {
        Timber.e(e, "Error converting FirebaseWifiCertificate to WifiCertificate")
        return null
    }
}
