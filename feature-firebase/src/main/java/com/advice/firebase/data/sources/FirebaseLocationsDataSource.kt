package com.advice.firebase.data.sources

import com.advice.core.local.Location
import com.advice.data.session.UserSession
import com.advice.data.sources.LocationsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toLocation
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.unwrapList
import com.advice.firebase.models.location.FirebaseLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseLocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val applicationScope: CoroutineScope,
) : LocationsDataSource {
    // todo: rewrite this to no turn a recursive function into a loop
    private val locations: StateFlow<List<Location>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                firestore
                    .collection("conferences")
                    .document(conference.code)
                    .collection("locations")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { querySnapshot ->
                        val locations =
                            querySnapshot
                                .toObjectsOrEmpty(FirebaseLocation::class.java)
                                .sortedBy { it.hierExtentLeft }
                        getChildrenNodes(locations)
                    }
                    .unwrapList("Failed to load locations")
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    override fun get(): Flow<List<Location>> = locations

    private fun getChildrenNodes(
        locations: List<FirebaseLocation>,
        parent: Long = 0,
    ): List<Location> {
        val nodes = locations.filter { it.parentId == parent }
        return nodes.mapNotNull {
            val children = getChildrenNodes(locations, it.id)
            it.toLocation(children)
        }
    }
}
