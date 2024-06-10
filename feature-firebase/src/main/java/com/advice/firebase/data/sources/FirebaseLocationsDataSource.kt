package com.advice.firebase.data.sources

import com.advice.core.local.Location
import com.advice.data.session.UserSession
import com.advice.data.sources.LocationsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toLocation
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.location.FirebaseLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseLocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : LocationsDataSource {
    override fun get(): Flow<List<Location>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("locations")
                .snapshotFlow()
                .map { querySnapshot ->
                    val locations =
                        querySnapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                            .sortedBy { it.hier_extent_left }

                    getChildrenNodes(locations, parent = 0)
                }
        }
    }

    private fun getChildrenNodes(
        locations: List<FirebaseLocation>,
        parent: Long,
    ): List<Location> {
        val nodes = locations.filter { it.parent_id == parent }
        return nodes.mapNotNull {
            val children = getChildrenNodes(locations, it.id)
            it.toLocation(children)
        }
    }
}
