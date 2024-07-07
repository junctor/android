package com.advice.data.sources

import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import kotlinx.coroutines.flow.Flow

interface FAQDataSource {

    fun get(): Flow<FlowResult<List<FAQ>>>
}
