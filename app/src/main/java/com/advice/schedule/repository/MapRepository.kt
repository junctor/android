package com.advice.schedule.repository

import com.advice.data.datasource.MapsDataSource

class MapRepository(
    private val dataSource: MapsDataSource,
) {

    val maps = dataSource.get()
}