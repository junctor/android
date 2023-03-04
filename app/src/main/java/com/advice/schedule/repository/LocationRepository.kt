package com.advice.schedule.repository

import com.advice.data.datasource.LocationsDataSource

class LocationRepository(private val locationsDataSource: LocationsDataSource) {

    val locations = locationsDataSource.get()
}