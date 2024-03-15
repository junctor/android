package com.advice.firebase.data.sources

import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.data.sources.VendorsDataSource
import kotlinx.coroutines.flow.combine

class FirebaseVendorsDataSource(
    private val organizationsDataSource: OrganizationsDataSource,
    private val tagsDataSource: TagsDataSource,
) : VendorsDataSource {

    override fun get() =
        combine(organizationsDataSource.get(), tagsDataSource.get()) { organizations, tags ->
            val vendor =
                tags.find { it.label == "Organization Type" }?.tags?.find { it.label == "Vendor" }
            organizations.filter { vendor == null || it.tags.contains(vendor.id) }
        }
}
