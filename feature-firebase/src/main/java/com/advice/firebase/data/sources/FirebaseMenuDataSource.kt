package com.advice.firebase.data.sources

import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle
import com.advice.data.session.UserSession
import com.advice.data.sources.MenuDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMenu
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseMenu
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FirebaseMenuDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : MenuDataSource {
    override fun get(): Flow<List<Menu>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("menus")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseMenu::class.java)
                        .mapNotNull { it.toMenu() }
                }
        }
    }
}