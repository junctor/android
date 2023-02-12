package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseConferenceMap
import com.advice.schedule.database.UserSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

class MapsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<FirebaseConferenceMap> {
    override fun get(): Flow<List<FirebaseConferenceMap>> {
        return userSession.conference.flatMapMerge { conference ->
            val list = conference.maps.map {
                FirebaseConferenceMap(it.name, it.file, null)
            }
            flow {
                TODO()

            }
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}