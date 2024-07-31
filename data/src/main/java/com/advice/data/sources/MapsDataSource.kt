package com.advice.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.Maps
import kotlinx.coroutines.flow.Flow

interface MapsDataSource {
    fun get(): Flow<FlowResult<Maps>>
}
