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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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
                .onStart { emit(emptyList()) }
            listener
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override fun get(): Flow<List<Speaker>> = speakers

    override suspend fun get(id: Long): Speaker? {
        val conference = userSession.currentConference ?: return null

        val snapshot = try {
            firestore.document("conferences/${conference.code}/speakers/$id")
                .get()
                .await()
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to get speaker with id: $id")
            return null
        }

        return snapshot.toObjectOrNull(FirebaseSpeaker::class.java)?.toSpeaker()
    }
}
