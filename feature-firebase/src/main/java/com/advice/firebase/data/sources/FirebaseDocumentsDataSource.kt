package com.advice.firebase.data.sources

import com.advice.core.local.Document
import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toDocument
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseDocument
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseDocumentsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : DocumentsDataSource {

    private val documents = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("documents")
            .snapshotFlow(analytics)
            .map {
                it.toObjectsOrEmpty(FirebaseDocument::class.java)
                    .mapNotNull { it.toDocument() }
            }
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    override fun get(): Flow<List<Document>> = documents
}
