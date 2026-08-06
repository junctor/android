package com.advice.firebase.extensions

import com.advice.core.local.Conference
import com.advice.firebase.telemetry.firestoreTelemetry
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal fun <T> Flow<T>.closeOnConferenceChange(conferenceFlow: Flow<Conference>): Flow<T> {
    val path = this.toString()
    return callbackFlow {
        var currentConference: Conference? = null
        val collector =
            launch {
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
    Timber.d("Snapshot listener for path: $path closed. ${firestoreTelemetry.activeListeners} active listeners.")
}

internal fun logFailure(
    path: String,
    error: FirebaseFirestoreException,
) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    Timber.e("Failed to get snapshot for path: $path, ${error.message}")
    if (error.code != FirebaseFirestoreException.Code.UNAVAILABLE) {
        crashlytics.log("Failed to get snapshot for path: $path")
        crashlytics.recordException(error)
    }
}

internal fun logSnapshot(
    path: String?,
    value: QuerySnapshot,
) {
    Timber.d("Snapshot received for path: $path, ${value.size()} documents, isFromCache: ${value.metadata.isFromCache}")
    val fromCache = value.metadata.isFromCache
    val total = firestoreTelemetry.onDocumentReads(value.size(), fromCache)
    val listeners = firestoreTelemetry.activeListeners
    if (!fromCache) {
        Timber.e("$listeners active snapshot listeners, document reads: $total(+${value.size()}) path: $path")
    } else {
        Timber.i(
            "CACHE: $listeners active snapshot listeners, document reads: $total(+0) path: $path (From Cache: ${value.size()})",
        )
    }
}
