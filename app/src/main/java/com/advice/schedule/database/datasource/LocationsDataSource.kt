package com.advice.schedule.database.datasource

import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseLocation
import com.advice.schedule.models.local.Location
import com.advice.schedule.toLocation
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class LocationsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<Location> {

    override fun get(): Flow<List<Location>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.LOCATIONS)
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseLocation::class.java)
                        .mapNotNull { it.toLocation() }
                }
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }
}