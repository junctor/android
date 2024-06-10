package com.advice.firebase.data.sources

import com.advice.core.local.Document
import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toDocument
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseDocument
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseDocumentsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : DocumentsDataSource {
    override fun get(): Flow<List<Document>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("documents")
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseDocument::class.java)
                        .mapNotNull { it.toDocument() }
                }
        }
    }
}
