package com.advice.firebase.data.sources

import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.session.UserSession
import com.advice.data.sources.FeedbackDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toFeedbackForm
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.unwrapList
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class FirebaseFeedbackDataSource(
    private val firestore: FirebaseFirestore,
    private val userSession: UserSession,
    private val applicationScope: CoroutineScope,
) : FeedbackDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val feedback: StateFlow<List<FeedbackForm>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                firestore
                    .collection("conferences/${conference.code}/feedbackforms")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { snapshot ->
                        snapshot
                            .toObjectsOrEmpty(FirebaseFeedbackForm::class.java)
                            .mapNotNull { it.toFeedbackForm() }
                    }
                    .unwrapList("Failed to load feedback forms")
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    override fun get(): Flow<List<FeedbackForm>> = feedback
}
