package com.advice.firebase.datasource

import com.advice.data.UserSession
import com.advice.data.datasource.ArticleDataSource
import com.advice.firebase.models.FirebaseArticle
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toArticle
import com.advice.firebase.toObjectsOrEmpty
import com.advice.schedule.models.local.Article
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseArticleDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : ArticleDataSource {
    override fun get(): Flow<List<Article>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("articles")
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseArticle::class.java)
                        .filter { !it.hidden || userSession.isDeveloper }
                        .mapNotNull { it.toArticle() }
                }
        }
    }
}