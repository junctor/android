package com.advice.firebase.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.data.session.UserSession
import com.advice.data.sources.MenuDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMenu
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.menu.FirebaseMenu
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseMenuDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val applicationScope: CoroutineScope,
) : MenuDataSource {
    private val menus: Flow<FlowResult<List<Menu>>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                // Emit Loading immediately on conference change so UI does not keep
                // showing the previous conference's menus while the new snapshot loads.
                firestore
                    .collection("conferences/${conference.code}/menus")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { querySnapshot ->
                        querySnapshot
                            .toObjectsOrEmpty(FirebaseMenu::class.java)
                            .mapNotNull { it.toMenu() }
                    }.onStart { emit(FlowResult.Loading) }
            }.shareIn(
                applicationScope,
                started = SharingStarted.Lazily,
                replay = 1,
            )

    override fun get(): Flow<FlowResult<List<Menu>>> = menus
}
