package com.advice.firebase.data.sources

import com.advice.core.audience.AudiencePolicy
import com.advice.core.local.Document
import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.firebase.extensions.SnapshotResult
import com.advice.firebase.extensions.audienceLabel
import com.advice.firebase.extensions.audienceRestriction
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toDocument
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseDocument
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseDocumentsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val audiencePolicy: AudiencePolicy,
) : DocumentsDataSource {
    private val documents =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                val snapshotFlow =
                    firestore
                        .collection("conferences")
                        .document(conference.code)
                        .collection("documents")
                        .snapshotFlow()
                        .closeOnConferenceChange(userSession.getConference())
                combine(snapshotFlow, userSession.audienceContext) { snapshotResult, context ->
                    when (snapshotResult) {
                        SnapshotResult.Loading -> emptyList()
                        is SnapshotResult.Failure -> {
                            Timber.e(snapshotResult.error, "Failed to load documents")
                            emptyList()
                        }

                        is SnapshotResult.Success -> {
                            snapshotResult.snapshot
                                .toObjectsOrEmpty(FirebaseDocument::class.java)
                                .filter {
                                    audiencePolicy.canView(
                                        it.audienceRestriction,
                                        context,
                                        it.audienceLabel,
                                    )
                                }
                                .mapNotNull { it.toDocument() }
                        }
                    }
                }
            }.shareIn(
                CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                replay = 1,
            )

    override fun get(): Flow<List<Document>> = documents

    override suspend fun get(id: Long): Document? {
        val conference = userSession.currentConference ?: return null

        val snapshot =
            try {
                firestore
                    .document("conferences/${conference.code}/documents/$id")
                    .get()
                    .await()
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to get document with id: $id")
                return null
            }

        val firebaseDocument = snapshot.toObjectOrNull(FirebaseDocument::class.java) ?: return null
        if (!audiencePolicy.canView(
                firebaseDocument.audienceRestriction,
                userSession.currentAudienceContext,
                firebaseDocument.audienceLabel,
            )
        ) {
            return null
        }
        return firebaseDocument.toDocument()
    }
}
