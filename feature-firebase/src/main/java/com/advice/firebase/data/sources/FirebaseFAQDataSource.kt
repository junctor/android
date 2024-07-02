package com.advice.firebase.data.sources

import com.advice.core.local.FAQ
import com.advice.data.session.UserSession
import com.advice.data.sources.FAQDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toFAQ
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseFAQ
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFAQDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : FAQDataSource {
    override fun get(): Flow<List<FAQ>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("faqs")
                .snapshotFlow(analytics)
                .map {
                    it.toObjectsOrEmpty(FirebaseFAQ::class.java).map { it.toFAQ() }
                }
        }
    }
}
