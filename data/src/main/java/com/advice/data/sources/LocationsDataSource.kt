package com.advice.data.sources

import com.advice.core.local.Location
import kotlinx.coroutines.flow.Flow

interface LocationsDataSource {

    fun get(): Flow<List<Location>>
}
