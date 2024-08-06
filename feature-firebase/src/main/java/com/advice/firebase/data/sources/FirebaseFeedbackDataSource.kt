package com.advice.firebase.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.session.UserSession
import com.advice.data.sources.FeedbackDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toFeedbackForm
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await

class FirebaseFeedbackDataSource(
    private val firestore: FirebaseFirestore,
    private val userSession: UserSession,
) : FeedbackDataSource {
    private val feedback: Flow<FlowResult<List<FeedbackForm>>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences/${conference.code}/feedbackforms")
                .snapshotFlow()
                .closeOnConferenceChange(userSession.getConference())
                .mapSnapshot {
                    it.toObjectsOrEmpty(FirebaseFeedbackForm::class.java)
                        .mapNotNull { it.toFeedbackForm() }
                }
                .onStart { emit(FlowResult.Loading) }
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override fun get(): Flow<FlowResult<List<FeedbackForm>>> = feedback

    override suspend fun fetch(conference: String): List<FeedbackForm> {
        val snapshot = firestore.collection("conferences/$conference/feedbackforms")
            .get(Source.CACHE)
            .await()

        return snapshot.toObjectsOrEmpty(FirebaseFeedbackForm::class.java)
            .mapNotNull { it.toFeedbackForm() }
    }
}
