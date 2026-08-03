package com.advice.organizations.data.repositories

import com.advice.core.local.Organization
import com.advice.data.sources.OrganizationsDataSource
import kotlinx.coroutines.flow.Flow

class OrganizationsRepository(
    private val organizationsDataSource: OrganizationsDataSource,
) {
    /** Upstream organizations datasource is already shared on the application scope. */
    val organizations: Flow<List<Organization>> = organizationsDataSource.get()

    suspend fun get(id: Long): Organization? = organizationsDataSource.get(id)
}
