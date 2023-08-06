package com.advice.schedule.data.repositories

import com.advice.core.local.Organization
import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class OrganizationsRepository(
    private val organizationsDataSource: OrganizationsDataSource,
) {
    suspend fun getOrganization(id: Long): Organization? {
        return organizations.first().find { it.id == id }
    }

    val organizations = organizationsDataSource.get()
}
