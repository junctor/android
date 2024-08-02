package com.advice.firebase.data.sources

import com.advice.core.local.NewsArticle
import com.advice.data.session.UserSession
import com.advice.data.sources.NewsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toArticle
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseArticle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseNewsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : NewsDataSource {
    private val articles: StateFlow<List<NewsArticle>> = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("articles")
            .snapshotFlowLegacy()
            .closeOnConferenceChange(userSession.getConference())
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseArticle::class.java)
                    .filter { !it.hidden || userSession.isDeveloper }
                    .sortedBy { it.updatedAt }
                    .mapNotNull { it.toArticle() }
            }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    override fun get(): Flow<List<NewsArticle>> = articles
}
