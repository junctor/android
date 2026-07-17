package com.advice.firebase.extensions

import com.advice.core.local.FlowResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber

fun CollectionReference.snapshotFlow(): Flow<SnapshotResult> {
    val path = this.path
    return callbackFlow {
        // Emit loading state
        trySend(SnapshotResult.Loading)
        listeners_count++
        // Create a listener
        val listenerRegistration =
            addSnapshotListener { value, error ->
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
            logSnapshotClosure(path)
            listenerRegistration.remove()
        }
    }
}

fun <T : Any> Flow<SnapshotResult>.mapSnapshot(block: (QuerySnapshot) -> T): Flow<FlowResult<T>> =
    map {
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

/**
 * Keeps List-returning datasource interfaces: Loading/Failure become empty lists
 * (matching prior silent-empty behavior from snapshotFlowLegacy).
 */
fun <T> Flow<FlowResult<List<T>>>.unwrapList(logMessage: String): Flow<List<T>> =
    map { result ->
        when (result) {
            is FlowResult.Success -> result.value
            FlowResult.Loading -> emptyList()
            is FlowResult.Failure -> {
                Timber.e(result.error, logMessage)
                emptyList()
            }
        }
    }
