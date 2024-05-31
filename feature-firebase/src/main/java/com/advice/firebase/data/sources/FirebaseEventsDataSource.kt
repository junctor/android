package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.Speaker
import com.advice.core.local.TagType
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toEvent
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class FirebaseEventsDataSource(
    private val userSession: UserSession,
    tagsDataSource: TagsDataSource,
    speakersDataSource: SpeakersDataSource,
    locationsDataSource: LocationsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val firestore: FirebaseFirestore,
) : EventsDataSource {

    private fun observeConferenceEvents(conference: Conference): Flow<List<FirebaseEvent>> {
        return firestore.collection("conferences")
            .document(conference.code)
            .collection("content")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
            }
    }

    private val conferenceAndTagsFlow = combine(
        userSession.getConference(),
        tagsDataSource.get(),
        speakersDataSource.get(),
        locationsDataSource.get(),
    ) { conference, tags, speakers, locations -> ConferenceState(conference, tags, speakers, locations) }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _eventsFlow =
        conferenceAndTagsFlow.flatMapLatest { (conference, tags, speakers, locations) ->
            combine(
                observeConferenceEvents(conference),
                bookmarkedEventsDataSource.get()
            ) { firebaseEvents, bookmarkedEvents ->
                firebaseEvents.mapNotNull {
                    it.toEvent(
                        tags = tags,
                        speakers = speakers,
                        isBookmarked = bookmarkedEvents.any { bookmark -> bookmark.id == it.id.toString() },
                        locations = locations
                    )
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

data class ConferenceState(
    val conference: Conference,
    val tags: List<TagType>,
    val speakers: List<Speaker>,
    val locations: List<Location>,
)
