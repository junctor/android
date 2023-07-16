package com.advice.schedule.data.repositories

import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource

class OrganizationsRepository(
    private val organizationsDataSource: OrganizationsDataSource,
    private val vendorsDataSource: VendorsDataSource,
    private val villagesDataSource: VillagesDataSource,
) {
    val organizations = organizationsDataSource.get()
    val vendors = vendorsDataSource.get()
    val villages = villagesDataSource.get()
}