package com.advice.firebase.data.sources

import com.advice.core.audience.AudiencePolicy
import com.advice.core.local.Speaker
import com.advice.data.session.UserSession
import com.advice.data.sources.SpeakersDataSource
import com.advice.firebase.extensions.SnapshotResult
import com.advice.firebase.extensions.audienceLabel
import com.advice.firebase.extensions.audienceRestriction
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toSpeaker
import com.advice.firebase.models.FirebaseSpeaker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseSpeakersDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val audiencePolicy: AudiencePolicy,
) : SpeakersDataSource {
    private val speakers: StateFlow<List<Speaker>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                val snapshotFlow =
                    firestore
                        .collection("conferences")
                        .document(conference.code)
                        .collection("speakers")
                        .snapshotFlow()
                        .closeOnConferenceChange(userSession.getConference())

                combine(snapshotFlow, userSession.audienceContext) { snapshotResult, context ->
                    when (snapshotResult) {
                        SnapshotResult.Loading -> emptyList()
                        is SnapshotResult.Failure -> {
                            Timber.e(snapshotResult.error, "Failed to load speakers")
                            emptyList()
                        }

                        is SnapshotResult.Success -> {
                            snapshotResult.snapshot
                                .toObjectsOrEmpty(FirebaseSpeaker::class.java)
                                .filter {
                                    (!it.hidden || userSession.isDeveloper) &&
                                        audiencePolicy.canView(
                                            it.audienceRestriction,
                                            context,
                                            it.audienceLabel,
                                        )
                                }.mapNotNull { it.toSpeaker() }
                                .sortedBy { it.name.lowercase(Locale.getDefault()) }
                        }
                    }
                }
            }.stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                initialValue = emptyList(),
            )

    override fun get(): Flow<List<Speaker>> = speakers

    override suspend fun get(id: Long): Speaker? {
        val conference = userSession.currentConference ?: return null
        val context = userSession.currentAudienceContext

        val snapshot =
            try {
                firestore
                    .document("conferences/${conference.code}/speakers/$id")
                    .get()
                    .await()
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to get speaker with id: $id")
                return null
            }

        val firebaseSpeaker = snapshot.toObjectOrNull(FirebaseSpeaker::class.java) ?: return null
        if ((!firebaseSpeaker.hidden || userSession.isDeveloper) &&
            audiencePolicy.canView(
                firebaseSpeaker.audienceRestriction,
                context,
                firebaseSpeaker.audienceLabel,
            )
        ) {
            return firebaseSpeaker.toSpeaker()
        }
        return null
    }
}
