package com.advice.schedule.database.datasource

import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FAQDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<FirebaseFAQ> {
    override fun get(): Flow<List<FirebaseFAQ>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.FAQS)
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseFAQ::class.java)
                }
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }
}