package com.advice.firebase.data.sources

import com.advice.core.local.NewsArticle
import com.advice.data.session.UserSession
import com.advice.data.sources.NewsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toArticle
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseArticle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseNewsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : NewsDataSource {
    override fun get(): Flow<List<NewsArticle>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("articles")
                .snapshotFlow(analytics)
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseArticle::class.java)
                        .filter { !it.hidden || userSession.isDeveloper }
                        .sortedBy { it.updated_at }
                        .mapNotNull { it.toArticle() }
                }
        }
    }
}
