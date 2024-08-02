package com.advice.firebase.extensions

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

var document_reads = 0
var document_cache_reads = 0
var listeners_count = 0

fun CollectionReference.snapshotFlowLegacy(): Flow<QuerySnapshot> {
    val path = this.path
    return callbackFlow {
        listeners_count++
        val listenerRegistration =
            addSnapshotListener { value, error ->
                if (error != null) {
                    logFailure(path, error)
                    close()
                    return@addSnapshotListener
                }
                if (value != null) {
                    logSnapshot(path, value)
                    trySend(value)
                }
            }
        awaitClose {
            listeners_count--
            listenerRegistration.remove()
        }
    }
}

internal fun logFailure(path: String, error: FirebaseFirestoreException) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    Timber.e("Failed to get snapshot for path: $path, ${error.message}")
    crashlytics.log("Failed to get snapshot for path: $path")
    crashlytics.recordException(error)
}

internal fun logSnapshot(path: String?, value: QuerySnapshot) {
    Timber.i("Snapshot received for path: $path, ${value.size()} documents, isFromCache: ${value.metadata.isFromCache}")
    if (!value.metadata.isFromCache) {
        document_reads += value.size()
        Timber.e("$listeners_count active listeners, document reads: $document_reads(+${value.size()}) path: $path")
    } else {
        document_cache_reads += value.size()
        Timber.i("$listeners_count active listeners, document reads: $document_reads(+0) path: $path (From Cache: ${value.size()})")
    }
}

