package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.data.sources.ConferencesDataSource
import com.advice.firebase.extensions.SnapshotResult
import com.advice.firebase.extensions.toConference
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseConference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

fun CollectionReference.snapshotFlow(): Flow<SnapshotResult> {
    return callbackFlow {
        // Emit loading state
        trySend(SnapshotResult.Loading)
        // Create a listener
        val listenerRegistration =
            addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(SnapshotResult.Failure(error))
                    close()
                    return@addSnapshotListener
                }
                if (value != null) {
                    trySend(SnapshotResult.Success(value))
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }
}


fun <T : Any> Flow<SnapshotResult>.mapSnapshot(
    block: (QuerySnapshot) -> T
): Flow<FlowResult<T>> {
    return map {
        when (it) {
            is SnapshotResult.Loading -> {
                FlowResult.Loading
            }

            is SnapshotResult.Failure -> {
                FlowResult.Failure(it.error)
            }

            is SnapshotResult.Success -> {
                val value = block(it.snapshot)
                FlowResult.Success(value)
            }
        }
    }
}

class FirebaseConferencesDataSource(
    private val firestore: FirebaseFirestore,
) : ConferencesDataSource {

    private val conferences: Flow<FlowResult<List<Conference>>> =
        firestore.collection("conferences")
            .snapshotFlow()
            .mapSnapshot {
                it.toObjectsOrEmpty(FirebaseConference::class.java).mapNotNull { it.toConference() }
            }

    override fun get(): Flow<FlowResult<List<Conference>>> = conferences
}
