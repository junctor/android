package com.advice.firebase.extensions

import com.advice.core.local.Action
import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Speaker
import com.advice.core.local.TagType
import com.advice.core.local.feedback.ContentFeedbackForm
import com.advice.core.local.feedback.FeedbackForm
import com.advice.firebase.models.FirebaseAction
import com.advice.firebase.models.FirebaseContent
import timber.log.Timber

fun FirebaseContent.toContents(
    code: String,
    tags: List<TagType>,
    speakers: List<Speaker>,
    bookmarkedEvents: List<Bookmark>,
    locations: List<Location>,
    feedbackforms: List<FeedbackForm>,
): Content? {
    try {
        val list = tags.flatMap { el -> el.tags.sortedBy { it.sortOrder } }

        val links = links.map { it.toAction() }
        val types =
            tagIds
                .mapNotNull { id ->
                    list.find { it.id == id }
                }.sortedBy { list.indexOf(it) }

        val speakers =
            people
                .map { person ->
                    val roles = person.tagIds.mapNotNull { id -> list.find { it.id == id } }
                    val speaker = speakers.find { it.id == person.personId }
                    person to speaker?.copy(roles = roles)
                }.sortedWith(compareBy({ it.first.sortOrder }, { it.second?.name }))
                .mapNotNull { it.second }

        val newSessions =
            sessions
                .mapNotNull { session ->
                    // if we cannot find the location, ignore this session
                    val location = locations.find { it.id == session.locationId }

                    if (location == null) {
                        Timber.e("Could not find location for session: $title")
                        return@mapNotNull null
                    }

                    val isBookmarked =
                        bookmarkedEvents.any { bookmark ->
                            bookmark is Bookmark.SessionBookmark &&
                                bookmark.id == session.sessionId.toString()
                        }

                    Session(
                        id = session.sessionId,
                        timeZone = session.timezoneName,
                        start = session.beginTimestamp.toDate().toInstant(),
                        end = session.endTimestamp.toDate().toInstant(),
                        location = location,
                        isBookmarked = isBookmarked,
                    )
                }.sortedBy { it.start }

        val isBookmarked =
            bookmarkedEvents.any { bookmark -> bookmark is Bookmark.ContentBookmark && bookmark.id == id.toString() }

        val feedback = feedbackforms.find { it.id == feedbackFormId }
        val feedbackForm =
            if (feedback != null) {
                ContentFeedbackForm(
                    enable = feedbackEnableTimestamp?.toDate()?.toInstant(),
                    disable = feedbackDisableTimestamp?.toDate()?.toInstant(),
                    form = feedback,
                )
            } else {
                null
            }

        return Content(
            id = id,
            conference = code,
            title = title,
            description = description,
            updated = updatedTimestamp.toDate().toInstant(),
            speakers = speakers,
            types = types,
            urls = links,
            media = media.mapNotNull { it.toMedia() },
            isBookmarked = isBookmarked,
            sessions = newSessions,
            feedback = feedbackForm,
            relatedContentIds = relatedContentIds ?: emptyList(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Content: ${ex.message}")
        return null
    }
}

private fun FirebaseAction.toAction() = Action(this.label, this.url)
