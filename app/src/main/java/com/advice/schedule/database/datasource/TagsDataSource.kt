package com.advice.schedule.database.datasource

import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class TagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<FirebaseTagType> {
    override fun get(): Flow<List<FirebaseTagType>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection("tagtypes")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseTagType::class.java)
                        .sortedBy { it.sort_order }
                }
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}