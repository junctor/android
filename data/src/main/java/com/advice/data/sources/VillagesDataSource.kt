package com.advice.data.sources

import com.advice.core.local.Organization
import kotlinx.coroutines.flow.Flow

interface VillagesDataSource {
    fun get(): Flow<List<Organization>>
}