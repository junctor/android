package com.advice.schedule.ui.information.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Location
import com.advice.core.local.LocationRow
import com.advice.core.ui.LocationsScreenState
import com.advice.schedule.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class LocationsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<LocationRepository>()

    private var _locations = MutableStateFlow<List<Location>>(emptyList())

    private val _state = MutableStateFlow(LocationsScreenState(emptyList()))
    val state = _state


    private val ping = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.locations.collect { locations ->
                Timber.e("locations: ${locations.size} ")
//                _locations.value = updateLocations(locations)

//                val list = flatten(locations)
//                Timber.e("flatten: list is now: ${list.size} long", )
                _state.value = LocationsScreenState(locations)
            }
        }
    }

    private fun flatten(locations: List<Location>): List<Location> {
        val list = mutableListOf<Location>()

        for (location in locations) {
            list.add(location)
            list.addAll(flatten(location.children))
        }
        return list.filter { it.isVisible }
    }

    private fun updateLocations(list: List<Location>): List<Location> {
        return list
//        return list.map { location ->
//            val children = location.getChildren()
//
//            val status = if (children.isEmpty()) {
//                location.getCurrentStatus()
//            } else {
//                // updating all children
//                children.forEach {
//                    it.setStatus(it.getCurrentStatus())
//                }
//
//                when {
//                    children.all { it.status == LocationStatus.Open } -> LocationStatus.Open
//                    children.all { it.status == LocationStatus.Closed } -> LocationStatus.Closed
//                    children.all { it.status == LocationStatus.Unknown } -> LocationStatus.Unknown
//                    else -> LocationStatus.Mixed
//                }
//            }
//            location.setStatus(status)
//        }
    }

    fun toggle(location: LocationRow) {
        viewModelScope.launch {
            val list = _locations.value.toMutableList()

            val isExpanded = !location.isExpanded
//            repository.expand(location.id, isExpanded)

            repository.expand(location.id, isExpanded)



//            val indexOf = list.indexOfFirst { it.id == location.id }
//        Timber.e("toggle: $list", )
//            if (indexOf != -1) {
                val isVisible = isExpanded
//                list[indexOf] = location.isChildrenExpanded(isVisible)

//                Timber.e("toggle: Found item, now: ${list[indexOf].title}")

//                repository.expand(location.id, isExpanded)
//
//                val children = location.children
//                Timber.e("item has ${children.size} children, setting isExpanded to: $isVisible")
//                for (child in children) {
////                    list[list.indexOf(child)] = child
////                        .isVisible(isVisible = isVisible)
//
//
//                    repository.visible(child.id, isVisible)
////                }
//

//                _locations.emit(list)
            }
        }

    companion object {
        private const val LOCATION_UPDATE_DELAY = 30_000L
    }
}