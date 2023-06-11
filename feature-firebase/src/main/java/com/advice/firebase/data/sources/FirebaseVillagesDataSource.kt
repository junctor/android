package com.advice.firebase.data.sources

import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.data.sources.VillagesDataSource
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