package com.advice.firebase.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.session.UserSession
import com.advice.data.sources.FeedbackDataSource
import com.advice.firebase.extensions.toFeedbackForm
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.shareIn

class FirebaseFeedbackDataSource(
    private val firestore: FirebaseFirestore,
    private val userSession: UserSession,
) : FeedbackDataSource {
    private val feedback: Flow<FlowResult<List<FeedbackForm>>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences/${conference.code}/feedbackforms")
                .snapshotFlow()
                .mapSnapshot {
                    it.toObjectsOrEmpty(FirebaseFeedbackForm::class.java)
                        .mapNotNull { it.toFeedbackForm() }
                }
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override fun get(): Flow<FlowResult<List<FeedbackForm>>> = feedback
}
