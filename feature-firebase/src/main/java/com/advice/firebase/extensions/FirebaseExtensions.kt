package com.advice.firebase.extensions

import com.advice.core.local.Conference
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
            logSnapshotClosure(path)
            listenerRegistration.remove()
        }
    }
}

internal fun <T> Flow<T>.closeOnConferenceChange(conferenceFlow: Flow<Conference>): Flow<T> {
    val path = this.toString()
    return callbackFlow {
        var currentConference: Conference? = null
        val collector = launch {
            conferenceFlow.collect { newConference ->
                // Assuming Conference has a proper equals() method
                if (newConference != currentConference) {
                    if (currentConference == null) {
                        currentConference = newConference
                    } else {
                        Timber.d("Closing snapshot listener for path: $path, conference changed.")
                        close()
                    }
                }
            }
        }

        collect { value ->
            trySend(value)
        }

        awaitClose {
            logSnapshotClosure(path)
            collector.cancel()
        }
    }
}


fun logSnapshotClosure(path: String) {
    Timber.d("Snapshot listener for path: $path closed. $listeners_count active listeners.")
}

internal fun logFailure(path: String, error: FirebaseFirestoreException) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    Timber.e("Failed to get snapshot for path: $path, ${error.message}")
    crashlytics.log("Failed to get snapshot for path: $path")
    crashlytics.recordException(error)
}

internal fun logSnapshot(path: String?, value: QuerySnapshot) {
    Timber.d("Snapshot received for path: $path, ${value.size()} documents, isFromCache: ${value.metadata.isFromCache}")
    if (!value.metadata.isFromCache) {
        document_reads += value.size()
        Timber.e("$listeners_count active snapshot listeners, document reads: $document_reads(+${value.size()}) path: $path")
    } else {
        document_cache_reads += value.size()
        Timber.i("CACHE: $listeners_count active snapshot listeners, document reads: $document_cache_reads(+0) path: $path (From Cache: ${value.size()})")
    }
}

