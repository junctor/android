package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseConference
import com.advice.core.local.Conference
import com.advice.schedule.App
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.toConference
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConferencesDataSource(private val firestore: FirebaseFirestore) : DataSource<Conference> {
    override fun get(): Flow<List<Conference>> {
        return firestore.collection(DatabaseManager.CONFERENCES)
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseConference::class.java)
                    .filter { !it.hidden || App.isDeveloper }
                    .mapNotNull { it.toConference() }
                    .sortedBy { it.startDate }
            }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}