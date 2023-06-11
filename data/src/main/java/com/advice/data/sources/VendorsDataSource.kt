package com.advice.data.sources

import com.advice.core.local.Organization
import kotlinx.coroutines.flow.Flow

interface VendorsDataSource {

    fun get(): Flow<List<Organization>>
}