package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.data.sources.ConferencesDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toConference
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseConference
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class FirebaseConferencesDataSource(
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : ConferencesDataSource {
    private val conferences = firestore.collection("conferences")
        .snapshotFlow(analytics)
        .map { querySnapshot ->
            querySnapshot.toObjectsOrEmpty(FirebaseConference::class.java)
                .filter { !it.hidden || it.developer }
                .mapNotNull { it.toConference() }
                .sortedByDescending { it.start }
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override fun get(): Flow<List<Conference>> = conferences
}
