package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.FeedbackDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.data.ConferenceState
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toContents
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseContentDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val tagsDataSource: TagsDataSource,
    private val speakersDataSource: SpeakersDataSource,
    private val locationsDataSource: LocationsDataSource,
    private val feedbackDataSource: FeedbackDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : ContentDataSource {

    private fun observeConferenceEvents(conference: Conference): Flow<List<FirebaseContent>> =
        firestore
            .collection("conferences")
            .document(conference.code)
            .collection("content")
            .snapshotFlowLegacy()
            .closeOnConferenceChange(userSession.getConference())
            .map { querySnapshot ->
                querySnapshot
                    .toObjectsOrEmpty(FirebaseContent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
            }

    private val conferenceAndTagsFlow =
        combine(
            userSession.getConference(),
            tagsDataSource.get(),
            speakersDataSource.get(),
            locationsDataSource.get(),
            feedbackDataSource.get(),
        ) { conference, tags, speakers, locations, feedbackforms ->
            ConferenceState(
                conference = conference,
                tags = tags,
                speakers = speakers,
                locations = locations.flatten(),
                feedbackforms = feedbackforms.toResultOrNull() ?: emptyList(),
            )
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private val _eventsFlow =
        conferenceAndTagsFlow
            .flatMapLatest { (conference, tags, speakers, locations, feedbackforms) ->
                combine(
                    observeConferenceEvents(conference),
                    bookmarkedEventsDataSource.get(),
                ) { firebaseEvents, bookmarkedEvents ->
                    val content = firebaseEvents.mapNotNull {
                        it.toContents(
                            code = conference.code,
                            tags = tags,
                            speakers = speakers,
                            bookmarkedEvents = bookmarkedEvents,
                            locations = locations,
                            feedbackforms = feedbackforms,
                        )
                    }
                    ConferenceContent(
                        content = content,
                    )
                }
            }.shareIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                replay = 1,
            )


    override fun get(): Flow<ConferenceContent> = _eventsFlow

    override suspend fun bookmark(content: Content) {
        bookmarkedEventsDataSource.bookmark(content, isBookmarked = !content.isBookmarked)
    }

    override suspend fun bookmark(session: Session) {
        bookmarkedEventsDataSource.bookmark(session, isBookmarked = !session.isBookmarked)
    }

    override suspend fun isBookmarked(content: Content): Boolean {
        return bookmarkedEventsDataSource.isBookmarked(content)
    }

    override suspend fun isBookmarked(session: Session): Boolean {
        return bookmarkedEventsDataSource.isBookmarked(session)
    }

    override suspend fun getContent(conference: String, contentId: Long): Content? {
        val tags = tagsDataSource.fetch(conference)
        val speakers = speakersDataSource.fetch(conference)
        val locations = locationsDataSource.fetch(conference).flatten()
        val bookmarks = bookmarkedEventsDataSource.get().first()
        val feedbackforms = feedbackDataSource.fetch(conference)

        val firebaseContent = getFirebaseContentOrNull(conference, contentId)
        if (firebaseContent == null) {
            Timber.e("Content not found: $contentId")
            return null
        }

        val content = firebaseContent.toContents(
            code = conference,
            tags = tags,
            speakers = speakers,
            bookmarkedEvents = bookmarks,
            locations = locations,
            feedbackforms = feedbackforms,
        )

        if (content == null) {
            Timber.e("Could not map FirebaseContent to Content: $contentId")
            return null
        }

        return content
    }

    override suspend fun getEvent(conference: String, contentId: Long, sessionId: Long): Event? {
        val content = getContent(conference, contentId)

        if (content == null) {
            Timber.e("Could not map FirebaseContent to Content: $contentId")
            return null
        }

        val session = content.sessions.find { it.id == sessionId }
        if (session == null) {
            Timber.e("Could not find session: $sessionId")
            return null
        }

        return Event(content, session)
    }

    private suspend fun getFirebaseContentOrNull(
        conference: String,
        id: Long,
    ): FirebaseContent? {
        val snapshot =
            firestore.collection("conferences")
                .document(conference)
                .collection("content")
                .document(id.toString())
                .get()
                .await()
        return snapshot.toObjectOrNull(FirebaseContent::class.java)
    }
}

// todo: this needs to be recursive.
private fun List<Location>.flatten(): List<Location> {
    return flatMap { location ->
        listOf(location) + location.children.flatten()
    }
}
