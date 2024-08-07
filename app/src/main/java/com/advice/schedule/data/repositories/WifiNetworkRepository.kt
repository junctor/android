package com.advice.schedule.data.repositories

import com.advice.data.sources.WiFiNetworksDataSource

class WifiNetworkRepository(
    private val wifiNetworksDataSource: WiFiNetworksDataSource,
) {
    fun get() = wifiNetworksDataSource.get()
    suspend fun get(id: Long) = wifiNetworksDataSource.get(id)
}
