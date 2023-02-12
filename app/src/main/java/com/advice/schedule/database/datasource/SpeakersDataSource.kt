package com.advice.schedule.database.datasource

import com.advice.schedule.App
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseSpeaker
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.toObjectsOrEmpty
import com.advice.schedule.toSpeaker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import java.util.Locale

class SpeakersDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<Speaker> {
    override fun get(): Flow<List<Speaker>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.SPEAKERS)
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseSpeaker::class.java)
                        .filter { !it.hidden || App.isDeveloper }
                        .mapNotNull { it.toSpeaker() }
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }
                }
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}