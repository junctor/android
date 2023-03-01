package com.advice.firebase.datasource

import com.advice.data.UserSession
import com.advice.data.datasource.LocationsDataSource
import com.advice.firebase.models.FirebaseLocation
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toLocation
import com.advice.firebase.toObjectsOrEmpty
import com.advice.schedule.models.local.Location
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseLocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : LocationsDataSource {

    override fun get(): Flow<List<Location>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("locations")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                        .mapNotNull { it.toLocation() }
                }
        }
    }
}