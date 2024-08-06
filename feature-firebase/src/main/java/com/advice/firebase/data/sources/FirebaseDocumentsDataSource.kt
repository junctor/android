package com.advice.firebase.data.sources

import com.advice.core.local.Document
import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
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
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseDocumentsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : DocumentsDataSource {

    private val documents = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("documents")
            .snapshotFlowLegacy()
            .closeOnConferenceChange(userSession.getConference())
            .map {
                it.toObjectsOrEmpty(FirebaseDocument::class.java)
                    .mapNotNull { it.toDocument() }
            }
            .onStart { emit(emptyList()) }
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    override fun get(): Flow<List<Document>> = documents

    override suspend fun get(id: Long): Document? {
        val conference = userSession.currentConference ?: return null

        val snapshot = try {
            firestore.document("conferences/${conference.code}/documents/$id")
                .get()
                .await()
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to get document with id: $id")
            return null
        }

        return snapshot.toObjectOrNull(FirebaseDocument::class.java)?.toDocument()
    }
}
