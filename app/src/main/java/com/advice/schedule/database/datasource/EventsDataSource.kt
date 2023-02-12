package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseEvent
import com.advice.schedule.App
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.local.Event
import com.advice.schedule.toEvent
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import timber.log.Timber

class EventsDataSource(
    private val userSession: UserSession,
    private val tagsDataSource: TagsDataSource,
    private val firestore: FirebaseFirestore
) : DataSource<Event> {

    private fun getEvents(conference: String): Flow<List<FirebaseEvent>> {
        return firestore.collection(DatabaseManager.CONFERENCES)
            .document(conference)
            .collection(DatabaseManager.EVENTS)
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { (!it.hidden || App.isDeveloper) }
            }
    }

    override fun get(): Flow<List<Event>> {
        return userSession.conference.flatMapMerge { conference ->
            combine(getEvents(conference.code), tagsDataSource.get()) { events, tags ->
                Timber.e("events: ${events.size}, tags: ${tags.size}")
                events.mapNotNull { it.toEvent(tags) }
            }
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}

fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listenerRegistration = addSnapshotListener { value, error ->
        if (error != null) {
            close()
            return@addSnapshotListener
        }
        if (value != null)
            trySend(value)
    }
    awaitClose {
        listenerRegistration.remove()
    }
}