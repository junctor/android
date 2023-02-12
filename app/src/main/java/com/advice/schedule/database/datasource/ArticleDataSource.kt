package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseArticle
import com.advice.schedule.App
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.local.Article
import com.advice.schedule.toArticle
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class ArticleDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<Article> {
    override fun get(): Flow<List<Article>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.ARTICLES)
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseArticle::class.java)
                        .filter { !it.hidden || App.isDeveloper }
                        .mapNotNull { it.toArticle() }
                }
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}