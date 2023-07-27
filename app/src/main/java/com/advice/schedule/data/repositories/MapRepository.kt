package com.advice.schedule.data.repositories

import com.advice.data.sources.MapsDataSource

class MapRepository(
    private val dataSource: MapsDataSource,
) {

    val maps = dataSource.get()
}
