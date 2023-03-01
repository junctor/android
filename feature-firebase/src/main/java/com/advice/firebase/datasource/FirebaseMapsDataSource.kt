package com.advice.firebase.datasource

import com.advice.core.firebase.FirebaseConferenceMap
import com.advice.data.UserSession
import com.advice.data.datasource.MapsDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

class FirebaseMapsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore

) : MapsDataSource {
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
}