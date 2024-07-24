package com.advice.firebase.extensions

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

var document_reads = 0
var document_cache_reads = 0
var listeners_count = 0

fun <T> QuerySnapshot.toObjectsOrEmpty(clazz: Class<T>): List<T> {
    return try {
        val result = mutableListOf<T>()
        for (d in this) {
            try {
                result.add(d.toObject(clazz))
            } catch (ex: Exception) {
                val path = (this.query as CollectionReference).path + "/${d.id}"
                Timber.e("Could not map $path to object: ${ex.message}")
            }
        }
        result
    } catch (ex: Exception) {
        Timber.e("Could not map data to objects: ${ex.message}")
        return emptyList()
    }
}

fun <T> DocumentSnapshot.toObjectOrNull(clazz: Class<T>): T? {
    return try {
        toObject(clazz)
    } catch (ex: Exception) {
        Timber.e("Could not map data to object: ${ex.message}")
        return null
    }
}


sealed class SnapshotResult {
    object Loading : SnapshotResult()
    data class Success(val snapshot: QuerySnapshot) : SnapshotResult()
    data class Failure(val error: Exception) : SnapshotResult()
}

fun CollectionReference.snapshotFlow(analytics: FirebaseAnalytics): Flow<QuerySnapshot> {
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
            listenerRegistration.remove()
        }
    }
}

private fun logFailure(path: String, error: FirebaseFirestoreException) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    Timber.e("Failed to get snapshot for path: $path, ${error.message}")
    crashlytics.log("Failed to get snapshot for path: $path")
    crashlytics.recordException(error)
}

private fun logSnapshot(path: String?, value: QuerySnapshot) {
    Timber.i("Snapshot received for path: $path, ${value.size()} documents, isFromCache: ${value.metadata.isFromCache}")
    if (!value.metadata.isFromCache) {
        document_reads += value.size()
        Timber.e("Created $listeners_count listeners, document reads: $document_reads(+${value.size()}) path: $path")
    } else {
        document_cache_reads += value.size()
        Timber.i("Created $listeners_count listeners, document reads: $document_reads(+0) path: $path (From Cache: ${value.size()})")
    }
}

