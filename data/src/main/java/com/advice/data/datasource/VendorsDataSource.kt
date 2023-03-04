package com.advice.data.datasource

import com.advice.core.local.Vendor
import kotlinx.coroutines.flow.Flow

interface VendorsDataSource {

    fun get(): Flow<List<Vendor>>
}