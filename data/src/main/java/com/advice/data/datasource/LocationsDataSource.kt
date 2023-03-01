package com.advice.data.datasource

import com.advice.schedule.models.local.Location
import kotlinx.coroutines.flow.Flow

interface LocationsDataSource {

    fun get(): Flow<List<Location>>
}