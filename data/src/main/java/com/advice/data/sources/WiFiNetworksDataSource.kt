package com.advice.data.sources

import com.advice.core.local.wifi.WiFiNetwork
import kotlinx.coroutines.flow.Flow

interface WiFiNetworksDataSource {
    fun get(): Flow<List<WiFiNetwork>>
}
