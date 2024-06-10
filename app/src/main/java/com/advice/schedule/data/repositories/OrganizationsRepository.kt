package com.advice.schedule.data.repositories

import com.advice.core.local.Organization
import com.advice.data.sources.OrganizationsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn

class OrganizationsRepository(
    private val organizationsDataSource: OrganizationsDataSource,
) {
    suspend fun find(id: Long): Organization? {
        return organizations.first().find { it.id == id }
    }

    val organizations =
        organizationsDataSource.get().shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            replay = 1,
        )
}
