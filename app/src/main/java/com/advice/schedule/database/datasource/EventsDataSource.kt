package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseBookmark
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
            combine(getEvents(conference.code), tagsDataSource.get(), getBookmarks()) { events, tags, bookmarks ->
                Timber.e("firebase events: ${events.size}, tags: ${tags.size}")
                val events = events.mapNotNull { it.toEvent(tags) }

                for (bookmark in bookmarks) {
                    events.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                }

                events.also {
                    Timber.e("events: ${it.size}")
                }
            }
        }
    }

    private fun getBookmarks(): Flow<List<FirebaseBookmark>> {
        return combine(userSession.user, userSession.conference) { user, conference ->
            TagsDataSource.MyObject(conference, user)
        }.flatMapMerge {
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(it.conference.code)
                .collection("users")
                .document(it.user!!.uid)
                .collection("bookmarks")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)
                }
        }
    }

    fun bookmark(event: Event) {
        Timber.e("Updating event selection: $event")

        val conference = userSession.currentConference
        val user = userSession.currentUser

        if (user == null) {
            Timber.e("User is null!")
            return
        }

        val document = firestore.collection(DatabaseManager.CONFERENCES)
            .document(conference.code)
            .collection(DatabaseManager.USERS)
            .document(user.uid)
            .collection("bookmarks")
            .document(event.id.toString())

        Timber.e("User: ${user.uid}, event: ${event.id}, conference: ${conference.code}")

        if (!event.isBookmarked) {
            Timber.e("Adding item")
            document.set(
                mapOf(
                    "id" to event.id.toString(),
                    "value" to true
                )
            )
        } else {
            Timber.e("Deleting item")
            document.delete()
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