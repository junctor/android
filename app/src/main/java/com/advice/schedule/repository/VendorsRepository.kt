package com.advice.schedule.repository

import com.advice.data.datasource.VendorsDataSource

class VendorsRepository(
    private val vendorsDataSource: VendorsDataSource
) {

    val vendors = vendorsDataSource.get()
}