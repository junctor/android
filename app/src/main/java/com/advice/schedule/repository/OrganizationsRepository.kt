package com.advice.schedule.repository

import com.advice.data.datasource.VendorsDataSource
import com.advice.data.datasource.VillagesDataSource

class OrganizationsRepository(
    private val vendorsDataSource: VendorsDataSource,
    private val villagesDataSource: VillagesDataSource,
) {

    val vendors = vendorsDataSource.get()
    val villages = villagesDataSource.get()
}