package com.advice.data.sources

import com.advice.core.local.Conference
import kotlinx.coroutines.flow.Flow

interface ConferencesDataSource {

    fun get(): Flow<List<Conference>>
}
