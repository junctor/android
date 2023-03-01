package com.advice.firebase.datasource

import com.advice.data.UserSession
import com.advice.data.datasource.SpeakersDataSource
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toObjectsOrEmpty
import com.advice.firebase.toSpeaker
import com.advice.schedule.models.local.Speaker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import java.util.Locale

class FirebaseSpeakersDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : SpeakersDataSource {

    override fun get(): Flow<List<Speaker>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("speakers")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseSpeaker::class.java)
                        .filter { !it.hidden || userSession.isDeveloper }
                        .mapNotNull { it.toSpeaker() }
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }
                }
        }
    }
}