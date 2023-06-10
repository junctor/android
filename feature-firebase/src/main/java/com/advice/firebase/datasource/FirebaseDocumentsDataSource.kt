package com.advice.firebase.datasource

import com.advice.core.local.Document
import com.advice.data.UserSession
import com.advice.data.datasource.DocumentsDataSource
import com.advice.firebase.models.FirebaseDocument
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toDocument
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class)
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