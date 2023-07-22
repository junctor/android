package com.advice.firebase.data.sources

import com.advice.core.local.Event
import com.advice.data.sources.EventDataSource
import com.advice.firebase.extensions.toEvent
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore,
) : EventDataSource {

    override suspend fun get(conference: String, id: Long): Event? {
        val snapshot = firestore.collection("conferences")
            .document(conference)
            .collection("events")
            .document(id.toString())
            .get()
            .await()
        // todo: add tags
        return snapshot.toObjectOrNull(FirebaseEvent::class.java)?.toEvent(emptyList())
    }
}