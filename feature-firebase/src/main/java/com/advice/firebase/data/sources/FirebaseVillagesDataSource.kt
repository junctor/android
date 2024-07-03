package com.advice.firebase.data.sources

import com.advice.core.local.TagType
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
            val village = tags.findTagByLabel("Village")
            organizations.filter { village == null || it.tags.contains(village.id) }
        }

    private fun List<TagType>.findTagByLabel(
        label: String
    ) = find { it.label == "Organization Type" }?.tags?.find {
        it.label == label
    }
}
