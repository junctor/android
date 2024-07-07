package com.advice.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import kotlinx.coroutines.flow.Flow

interface ConferencesDataSource {

    fun get(): Flow<FlowResult<List<Conference>>>
}
