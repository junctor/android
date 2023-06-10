package com.advice.firebase.datasource

import com.advice.core.local.Event
import com.advice.data.UserSession
import com.advice.data.datasource.BookmarkedElementDataSource
import com.advice.data.datasource.EventsDataSource
import com.advice.data.datasource.TagsDataSource
import com.advice.firebase.models.FirebaseEvent
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toEvent
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@OptIn(FlowPreview::class)
class FirebaseEventsDataSource(
    private val userSession: UserSession,
    private val tagsDataSource: TagsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val firestore: FirebaseFirestore,
) : EventsDataSource {

    private val _eventsFlow = combine(
        userSession.getConference(),
        tagsDataSource.get(),
        bookmarkedEventsDataSource.get()
    ) { conference, tags, bookmarkedEvents ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("events")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
                    .mapNotNull {
                        it.toEvent(
                            tags = tags,
                            isBookmarked = bookmarkedEvents.any { bookmark -> bookmark.id == it.id.toString() })
                    }
            }
    }.flattenMerge()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1
        )

    override fun get(): Flow<List<Event>> = _eventsFlow


    override suspend fun bookmark(event: Event) {
        bookmarkedEventsDataSource.bookmark(event.id, isBookmarked = !event.isBookmarked)
    }
}

