package com.advice.firebase.datasource

import com.advice.core.local.ConferenceMap
import com.advice.data.UserSession
import com.advice.data.datasource.MapsDataSource
import com.advice.firebase.models.FirebaseConferenceMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

class FirebaseMapsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore

) : MapsDataSource {
    override fun get(): Flow<List<ConferenceMap>> {
        return userSession.getConference().flatMapMerge { conference ->
            val list = conference.maps.map {
                FirebaseConferenceMap(it.name, it.file, null)
            }
            flow {
                TODO()

            }
        }
    }
}