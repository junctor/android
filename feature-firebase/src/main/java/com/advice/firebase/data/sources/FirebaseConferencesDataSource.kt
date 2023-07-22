package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.data.sources.ConferencesDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toConference
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseConference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseConferencesDataSource(
    private val firestore: FirebaseFirestore,
) : ConferencesDataSource {

    override fun get(): Flow<List<Conference>> {
        return firestore.collection("conferences")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseConference::class.java)
                    .filter { !it.hidden || it.developer }
                    .mapNotNull { it.toConference() }
                    .sortedByDescending { it.start }
            }
    }
}