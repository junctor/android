package com.advice.data.sources

import com.advice.core.local.Organization
import kotlinx.coroutines.flow.Flow

interface OrganizationsDataSource {

    fun get(): Flow<List<Organization>>
}
