package com.advice.firebase.extensions

import com.google.firebase.firestore.QuerySnapshot

sealed class SnapshotResult {
    data object Loading : SnapshotResult()
    data class Success(val snapshot: QuerySnapshot) : SnapshotResult()
    data class Failure(val error: Exception) : SnapshotResult()
}
