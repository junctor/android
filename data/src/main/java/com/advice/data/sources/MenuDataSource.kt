package com.advice.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import kotlinx.coroutines.flow.Flow

interface MenuDataSource {
    fun get(): Flow<FlowResult<List<Menu>>>
}
