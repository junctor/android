package com.advice.schedule.data.repositories

import com.advice.core.local.Organization
import com.advice.data.sources.OrganizationsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class OrganizationsRepository(
    private val organizationsDataSource: OrganizationsDataSource,
) {

    val organizations: Flow<List<Organization>> =
        organizationsDataSource.get().shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    suspend fun get(id: Long): Organization? = organizationsDataSource.get(id)
}
