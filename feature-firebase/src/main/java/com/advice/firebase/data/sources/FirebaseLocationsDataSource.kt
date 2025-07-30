package com.advice.firebase.data.sources

import com.advice.core.local.Location
import com.advice.data.session.UserSession
import com.advice.data.sources.LocationsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toLocation
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.location.FirebaseLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseLocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : LocationsDataSource {

    // todo: rewrite this to no turn a recursive function into a loop
    private val locations: StateFlow<List<Location>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("locations")
                .snapshotFlowLegacy()
                .closeOnConferenceChange(userSession.getConference())
                .map { querySnapshot ->
                    val locations =
                        querySnapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                            .sortedBy { it.hierExtentLeft }
                    getChildrenNodes(locations)
                }
                .onStart { emit(emptyList()) }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
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
