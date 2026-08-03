package com.advice.firebase.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.NewsArticle
import com.advice.data.session.UserSession
import com.advice.data.sources.NewsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toArticle
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseArticle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseNewsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    applicationScope: CoroutineScope,
) : NewsDataSource {
    private val articles: StateFlow<FlowResult<List<NewsArticle>>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                firestore
                    .collection("conferences")
                    .document(conference.code)
                    .collection("articles")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { querySnapshot ->
                        querySnapshot
                            .toObjectsOrEmpty(FirebaseArticle::class.java)
                            .filter { !it.hidden || userSession.isDeveloper }
                            .sortedByDescending { it.updatedAt }
                            .mapNotNull { it.toArticle() }
                    }
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = FlowResult.Loading,
            )

    override fun get(): Flow<FlowResult<List<NewsArticle>>> = articles
}
