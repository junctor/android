package com.advice.data.sources

import com.advice.core.local.wifi.WirelessNetwork
import kotlinx.coroutines.flow.Flow

interface WiFiNetworksDataSource {
    fun get(): Flow<List<WirelessNetwork>>
    suspend fun get(id: Long): WirelessNetwork?
}
