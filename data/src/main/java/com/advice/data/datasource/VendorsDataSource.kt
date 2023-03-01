package com.advice.data.datasource

import com.advice.schedule.models.local.Vendor
import kotlinx.coroutines.flow.Flow

interface VendorsDataSource {

    fun get(): Flow<List<Vendor>>
}