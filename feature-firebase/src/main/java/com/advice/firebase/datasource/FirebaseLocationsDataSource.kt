package com.advice.firebase.datasource

import com.advice.core.local.Location
import com.advice.data.UserSession
import com.advice.data.datasource.LocationsDataSource
import com.advice.firebase.models.FirebaseLocation
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toLocation
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseLocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : LocationsDataSource {

    override fun get(): Flow<List<Location>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("locations")
                .snapshotFlow()
                .map { querySnapshot ->
                    val locations = querySnapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                        .sortedBy { it.hier_extent_left }

                    getChildrenNodes(locations, parent = 0)
                }
        }
    }

    private fun getChildrenNodes(locations: List<FirebaseLocation>, parent: Long): List<Location> {
        val nodes = locations.filter { it.parent_id == parent }
        return nodes.mapNotNull {
            val children = getChildrenNodes(locations, it.id)
            it.toLocation(children)
        }
    }
}