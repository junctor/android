package com.advice.firebase.datasource

import com.advice.data.datasource.OrganizationsDataSource
import com.advice.data.datasource.TagsDataSource
import com.advice.data.datasource.VillagesDataSource
import kotlinx.coroutines.flow.combine

class FirebaseVillagesDataSource(
    private val organizationsDataSource: OrganizationsDataSource,
    private val tagsDataSource: TagsDataSource,
) : VillagesDataSource {

    override fun get() =
        combine(organizationsDataSource.get(), tagsDataSource.get()) { organizations, tags ->
            val vendor =
                tags.find { it.label == "Organization Type" }?.tags?.find { it.label == "Village" }
            organizations.filter { vendor == null || it.tags.contains(vendor.id) }
        }
}