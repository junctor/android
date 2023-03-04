package com.advice.schedule.ui.information.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Location
import com.advice.core.local.LocationContainer
import com.advice.core.local.LocationStatus
import com.advice.core.local.hasChildren
import com.advice.core.local.isChildrenExpanded
import com.advice.core.local.isExpanded
import com.advice.core.local.setStatus
import com.advice.schedule.repository.LocationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LocationsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<LocationRepository>()

    private val _locations = MutableStateFlow<List<LocationContainer>>(emptyList())

    private val ping = MutableStateFlow(false)

    val locations = combine(repository.locations, ping) { locations, _ ->
        val list = locations.sortedWith(compareBy({ it.hierExtentLeft }, { it.hierExtentRight }))
        val elements = list.map { element -> element.toContainer(list.any { it.parent == element.id }) }
        updateLocations(elements).also {
            Timber.e("${it.take(10)}: ")
        }
    }

    init {
        viewModelScope.launch {
            while (isActive) {
                Timber.e(("Updating location list"))
                delay(LOCATION_UPDATE_DELAY)
                ping.emit(!ping.value)
            }
        }
    }

    private fun updateLocations(list: List<LocationContainer>): List<LocationContainer> {
        return list.map { location ->
            val children = location.getChildren()

            val status = if (children.isEmpty()) {
                location.getCurrentStatus()
            } else {
                // updating all children
                children.forEach {
                    it.setStatus(it.getCurrentStatus())
                }

                when {
                    children.all { it.status == LocationStatus.Open } -> LocationStatus.Open
                    children.all { it.status == LocationStatus.Closed } -> LocationStatus.Closed
                    children.all { it.status == LocationStatus.Unknown } -> LocationStatus.Unknown
                    else -> LocationStatus.Mixed
                }
            }
            location.setStatus(status)
        }
    }

    fun toggle(location: LocationContainer) {
        val list = _locations.value.toMutableList()

        val indexOf = list.indexOf(location)
        val isExpanded = !list[indexOf].isChildrenExpanded
        list[indexOf] = location.isChildrenExpanded(isExpanded)

        val children = location.getChildren()
        for (child in children) {
            list[list.indexOf(child)] = child
                .isExpanded(isExpanded = isExpanded)
                .isChildrenExpanded(isExpanded = isExpanded)
        }

        _locations.value = list
    }

    private fun LocationContainer.getChildren(): List<LocationContainer> {
        val list = _locations.value

        var index = list.indexOf(this)
        if (index == -1)
            return emptyList()

        val result = mutableListOf<LocationContainer>()

        while (++index < list.size && list[index].depth > depth) {
            result.add(list[index])
        }
        return result
    }

    companion object {
        private const val LOCATION_UPDATE_DELAY = 30_000L
    }
}

fun Location.toContainer(hasChildren: Boolean = false): LocationContainer {
    return LocationContainer(id, name, shortName, defaultStatus, depth, schedule ?: emptyList(), isExpanded = true, isChildrenExpanded = true)
        .hasChildren(hasChildren)
}

// todo: refactor LocationsViewModel to store the raw Locations instead of recreating this item when clicked.
fun LocationContainer.toLocation(): Location {
    return Location(id, title, shortTitle, null, "", defaultStatus, depth, -1, -1, -1, -1, null)
}