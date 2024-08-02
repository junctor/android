package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.data.sources.ConferencesDataSource
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toConference
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseConference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow


class FirebaseConferencesDataSource(
    private val firestore: FirebaseFirestore,
) : ConferencesDataSource {

    private val conferences: Flow<FlowResult<List<Conference>>> =
        firestore.collection("conferences")
            .snapshotFlow()
            .mapSnapshot {
                it.toObjectsOrEmpty(FirebaseConference::class.java)
                    .mapNotNull { it.toConference() }
                    .sortedWith(compareBy({ it.hasFinished }, { it.start }))
            }

    override fun get(): Flow<FlowResult<List<Conference>>> = conferences
}
