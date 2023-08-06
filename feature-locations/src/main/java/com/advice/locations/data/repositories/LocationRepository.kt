package com.advice.locations.data.repositories

import com.advice.core.local.Location
import com.advice.core.local.LocationRow
import com.advice.data.sources.LocationsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class LocationRepository(private val locationsDataSource: LocationsDataSource) {

    private val expanded = MutableStateFlow(emptyList<Long>())

    val locations = combine(
        locationsDataSource.get(),
        expanded,
    ) { locations, expandedIds ->
        val updated = locations.filter { it.parent == 0L }.flatMap {
            val flatten = flatten(it)

//            if (expandedIds.isEmpty()) {
//                expanded.value = flatten.map { it.id }
//            }

            flatten.map { location ->
                val isVisible = isVisible(location, expandedIds, flatten)
                location.copy(isVisible = true, isExpanded = true)
            }
        }

        return@combine updated.filter { it.isVisible }.map {
            LocationRow(
                id = it.id,
                title = it.shortName ?: it.name,
                status = it.status,
                depth = it.depth,
                hasChildren = it.hasChildren,
                isExpanded = it.isExpanded,
                schedule = it.schedule ?: emptyList()
            )
        }
    }

    private fun isVisible(
        location: Location,
        expandedIds: List<Long>,
        locations: List<Location>
    ): Boolean {
        // Root is always visible
        if (location.parent == 0L)
            return true

        // Nothing is expanded or collapsed
        if (expandedIds.isEmpty())
            return true

        return (location.allParents(locations) { id in expandedIds } && location.parent in expandedIds)
    }

    private fun flatten(root: Location): List<Location> {
        val temp = mutableListOf<Location>()

        temp.add(root)
        for (location in root.children) {
            temp.addAll(flatten(location))
        }

        return temp
    }

    private fun Location.allParents(
        locations: List<Location>,
        predicate: Location.() -> Boolean
    ): Boolean {
        var node: Location? = locations.find { it.id == parent }
        while (node != null) {
            if (!node.predicate()) {
                return false
            }
            node = locations.find { it.id == node!!.parent }
        }
        return true
    }

    suspend fun collapse(id: Long) {
        val list = expanded.value.toMutableList()
        list.remove(id)
        expanded.emit(list)
    }

    suspend fun expand(id: Long) {
        val list = expanded.value.toMutableList()
        list.add(id)
        expanded.emit(list)
    }
}
