package com.advice.maps.data.repositories

import com.advice.data.sources.MapsDataSource

class MapsRepository(
    dataSource: MapsDataSource,
) {
    val maps = dataSource.get()
}
