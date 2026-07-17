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
                        buildLocationTree(locations)
                    }
                    .unwrapList("Failed to load locations")
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    override fun get(): Flow<List<Location>> = locations

    /**
     * Builds a location tree iteratively by constructing deepest nodes first so each
     * parent's children are already available when it is mapped.
     */
    private fun buildLocationTree(locations: List<FirebaseLocation>): List<Location> {
        val byParent = locations.groupBy { it.parentId }
        val built = HashMap<Long, Location>(locations.size)

        for (firebaseLocation in locations.sortedByDescending { it.hierDepth }) {
            val children = byParent[firebaseLocation.id]
                .orEmpty()
                .mapNotNull { built[it.id] }
            val location = firebaseLocation.toLocation(children) ?: continue
            built[firebaseLocation.id] = location
        }

        return byParent[0L].orEmpty().mapNotNull { built[it.id] }
    }
}
