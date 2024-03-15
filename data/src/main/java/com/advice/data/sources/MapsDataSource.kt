package com.advice.data.sources

import com.advice.core.local.MapFile
import kotlinx.coroutines.flow.Flow

interface MapsDataSource {

    fun get(): Flow<List<MapFile>>
}
