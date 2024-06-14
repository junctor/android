package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.data.ConferenceState
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toContents
import com.advice.firebase.extensions.toEvents
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseContent
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

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseEventsDataSource(
    private val userSession: UserSession,
    tagsDataSource: TagsDataSource,
    speakersDataSource: SpeakersDataSource,
    locationsDataSource: LocationsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val firestore: FirebaseFirestore,
) : EventsDataSource {
    private fun observeConferenceEvents(conference: Conference): Flow<List<FirebaseContent>> {
        return firestore.collection("conferences")
            .document(conference.code)
            .collection("content")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseContent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
            }
    }

    private val conferenceAndTagsFlow =
        combine(
            userSession.getConference(),
            tagsDataSource.get(),
            speakersDataSource.get(),
            locationsDataSource.get(),
        ) { conference, tags, speakers, locations ->
            ConferenceState(
                conference,
                tags,
                speakers,
                locations.flatten(),
            )
        }
            .shareIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                replay = 1,
            )

    private val _eventsFlow =
        conferenceAndTagsFlow.flatMapLatest { (conference, tags, speakers, locations) ->
            combine(
                observeConferenceEvents(conference),
                bookmarkedEventsDataSource.get(),
            ) { firebaseEvents, bookmarkedEvents ->
                val (unscheduled, scheduled) = firebaseEvents.partition { it.sessions.isEmpty() }

                ConferenceContent(
                    events =
                        scheduled.mapNotNull {
                            it.toEvents(
                                conference = conference.code,
                                tags = tags,
                                speakers = speakers,
                                bookmarkedEvents = bookmarkedEvents,
                                locations = locations,
                            )
                        }.flatten(),
                    content =
                        unscheduled.mapNotNull {
                            it.toContents(
                                conference.name,
                                tags,
                                speakers,
                                bookmarkedEvents,
                            )
                        },
                )
            }
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    override fun get(): Flow<ConferenceContent> = _eventsFlow

    override suspend fun bookmark(event: Event) {
        bookmarkedEventsDataSource.bookmark(event.id, isBookmarked = !event.isBookmarked)
    }
}

// todo: this needs to be recursive.
private fun List<Location>.flatten(): List<Location> {
    return flatMap { location ->
        listOf(location) + location.children.flatten()
    }
}
