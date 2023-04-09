package com.advice.schedule.repository

import com.advice.core.local.Location
import com.advice.core.local.LocationRow
import com.advice.core.local.isVisible
import com.advice.data.datasource.LocationsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class LocationRepository(private val locationsDataSource: LocationsDataSource) {

    private val expanded = MutableStateFlow(emptyList<Long>())
    private val visible = MutableStateFlow(emptyList<Long>())

    private val _locations = MutableStateFlow(emptyList<Location>())

    val locations = combine(
        locationsDataSource.get(),
        expanded,
        visible
    ) { locations, expandedIds, visibleIds ->
        // Storing all the location ids
        if (visibleIds.isEmpty()) {
            visible.value = ids(locations)
        }
        if (expandedIds.isEmpty()) {
            expanded.value = ids(locations)
        }

        _locations.value = locations


        Timber.e("locations: ${locations.size}, ids: ${visibleIds}, ")

        val temp = setVisibility(locations, visibleIds)
        for (location in temp) {
            Timber.e(location.toString())
        }

        return@combine temp.filter { it.isVisible }.map {
            LocationRow(it.id, it.name, it.status, it.depth, it.hasChildren, it.isExpanded)
        }

//        temp
    }

    private fun setVisibility(locations: List<Location>, visibleIds: List<Long>): List<Location> {
        val temp = mutableListOf<Location>()

        for (location in locations) {
            val isVisible = visibleIds.isEmpty() || location.id in visibleIds
            Timber.e("setVisibility: ${location.name} - $isVisible")
            location.isVisible = isVisible
            temp.add(location)
            temp.addAll(setVisibility(location.children, visibleIds))

        }
        return temp
    }

    private fun ids(locations: List<Location>): List<Long> {
        val ids = mutableListOf<Long>()

        for (location in locations) {
            ids.add(location.id)
            ids.addAll(ids(location.children))
        }

        return ids
    }

    suspend fun visible(id: Long, isVisible: Boolean) {
        val list = visible.value.toMutableList()
        Timber.e("visible: list was ${list.size}")
        if (isVisible) {
            list.add(id)
        } else {
            list.remove(id)
        }

        Timber.e("visible: $id, $isVisible, list is now: ${list.size}")
        visible.emit(list)
    }

    suspend fun expand(id: Long, isExpanded: Boolean) {
        Timber.e("expand: $id, $isExpanded")
        val list = _locations.value

        for (location in list) {
            if (location.id == id) {
                Timber.e("expand: found: $location", )
                expand(location.children, isExpanded)
            } else {
                for (child in location.children) {
                    expand(child.id, isExpanded)
                }
            }
        }
    }

    private suspend fun expand(nodes: List<Location>, isExpanded: Boolean) {
        for (node in nodes) {
            visible(node.id, !isExpanded)
            expand(node.children, isExpanded)
        }
    }
}