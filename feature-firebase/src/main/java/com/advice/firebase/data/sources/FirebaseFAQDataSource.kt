package com.advice.firebase.data.sources

import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import com.advice.data.session.UserSession
import com.advice.data.sources.FAQDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toFAQ
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseFAQ
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFAQDataSource(
    private val firestore: FirebaseFirestore,
    private val userSession: UserSession,
) : FAQDataSource {
    private val faqs: Flow<FlowResult<List<FAQ>>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences/${conference.code}/faqs")
                .snapshotFlow()
                .closeOnConferenceChange(userSession.getConference())
                .mapSnapshot {
                    it.toObjectsOrEmpty(FirebaseFAQ::class.java).map { it.toFAQ() }
                }
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override fun get(): Flow<FlowResult<List<FAQ>>> = faqs
}
