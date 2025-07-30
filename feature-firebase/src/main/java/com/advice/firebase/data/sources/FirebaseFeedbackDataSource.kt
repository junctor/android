package com.advice.firebase.data.sources

import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.session.UserSession
import com.advice.data.sources.FeedbackDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toFeedbackForm
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class FirebaseFeedbackDataSource(
    private val firestore: FirebaseFirestore,
    private val userSession: UserSession,
) : FeedbackDataSource {
    private val feedback: StateFlow<List<FeedbackForm>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences/${conference.code}/feedbackforms")
                .snapshotFlowLegacy()
                .closeOnConferenceChange(userSession.getConference())
                .map {
                    it.toObjectsOrEmpty(FirebaseFeedbackForm::class.java)
                        .mapNotNull { it.toFeedbackForm() }
                }
                .onStart { emit(emptyList()) }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    override fun get(): Flow<List<FeedbackForm>> = feedback
}
