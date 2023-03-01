package com.advice.firebase.datasource

import com.advice.core.firebase.FirebaseConference
import com.advice.core.local.Conference
import com.advice.data.datasource.ConferencesDataSource
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toConference
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseConferencesDataSource(private val firestore: FirebaseFirestore) : ConferencesDataSource {
    override fun get(): Flow<List<Conference>> {
        return firestore.collection("conferences")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseConference::class.java)
                    .filter { !it.hidden }
                    .mapNotNull { it.toConference() }
                    .sortedBy { it.startDate }
            }
    }
}