package com.advice.firebase.datasource

import com.advice.core.local.FAQ
import com.advice.data.UserSession
import com.advice.data.datasource.FAQDataSource
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toFAQ
import com.advice.firebase.toObjectsOrEmpty
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseFAQDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : FAQDataSource {

    override fun get(): Flow<List<FAQ>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("faqs")
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseFAQ::class.java).map { it.toFAQ() }
                }
        }
    }
}

