package com.advice.firebase.extensions

import com.advice.core.local.FlowResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

fun CollectionReference.snapshotFlow(): Flow<SnapshotResult> {
    val path = this.path
    return callbackFlow {
        // Emit loading state
        trySend(SnapshotResult.Loading)
        // Create a listener
        val listenerRegistration =
            addSnapshotListener { value, error ->
                listeners_count++
                if (error != null) {
                    logFailure(path, error)
                    trySend(SnapshotResult.Failure(error))
                    close()
                    return@addSnapshotListener
                }
                if (value != null) {
                    logSnapshot(path, value)
                    trySend(SnapshotResult.Success(value))
                }
            }
        awaitClose {
            listeners_count--
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
