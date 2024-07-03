package com.advice.firebase.data.sources

import com.advice.core.local.Menu
import com.advice.data.session.UserSession
import com.advice.data.sources.MenuDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMenu
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.menu.FirebaseMenu
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseMenuDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : MenuDataSource {

    // todo: investigate a better way to return an init state when the conference has changed.
    private val menus: Flow<List<Menu>> = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("menus")
            .snapshotFlow(analytics)
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseMenu::class.java)
                    .mapNotNull { it.toMenu() }
            }
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    override fun get(): Flow<List<Menu>> = menus
}
