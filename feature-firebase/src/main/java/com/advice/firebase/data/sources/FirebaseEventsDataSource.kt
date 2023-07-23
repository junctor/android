package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.Event
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.EventDataSource
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toEvent
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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

    private fun observeConferenceEvents(conference: Conference): Flow<List<FirebaseEvent>> {
        return firestore.collection("conferences")
            .document(conference.code)
            .collection("events")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
            }
    }

    val conferenceAndTagsFlow = combine(
        userSession.getConference(),
        tagsDataSource.get()
    ) { conference, tags -> Pair(conference, tags) }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _eventsFlow = conferenceAndTagsFlow.flatMapLatest { (conference, tags) ->
        combine(
            observeConferenceEvents(conference),
            bookmarkedEventsDataSource.get()
        ) { firebaseEvents, bookmarkedEvents ->
            firebaseEvents.mapNotNull {
                it.toEvent(
                    tags = tags,
                    isBookmarked = bookmarkedEvents.any { bookmark -> bookmark.id == it.id.toString() })
            }
        }
    }
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

