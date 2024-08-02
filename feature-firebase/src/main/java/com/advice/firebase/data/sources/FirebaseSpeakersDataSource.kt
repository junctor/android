package com.advice.firebase.data.sources

import com.advice.core.local.Speaker
import com.advice.data.session.UserSession
import com.advice.data.sources.SpeakersDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toSpeaker
import com.advice.firebase.models.FirebaseSpeaker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseSpeakersDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : SpeakersDataSource {
    private val speakers: StateFlow<List<Speaker>> =
        userSession.getConference().flatMapMerge { conference ->
            val listener = firestore.collection("conferences")
                .document(conference.code)
                .collection("speakers")
                .snapshotFlowLegacy()
                .closeOnConferenceChange(userSession.getConference())
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseSpeaker::class.java)
                        .filter { !it.hidden || userSession.isDeveloper }
                        .mapNotNull { it.toSpeaker() }
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }
                }
            listener
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override fun get(): Flow<List<Speaker>> = speakers

    override suspend fun fetch(conference: String): List<Speaker> {
        val snapshot = firestore.collection("conferences")
            .document(conference)
            .collection("speakers")
            .get(Source.CACHE)
            .await()

        return snapshot.toObjectsOrEmpty(FirebaseSpeaker::class.java)
            .filter { !it.hidden || userSession.isDeveloper }
            .mapNotNull { it.toSpeaker() }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    override suspend fun get(id: Long): Speaker? {
        val conference = userSession.currentConference ?: return null

        val snapshot = firestore.document("conferences/${conference.code}/speakers/$id")
            .get()
            .await()

        return snapshot.toObjectOrNull(FirebaseSpeaker::class.java)?.toSpeaker()
    }
}
