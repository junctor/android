package com.advice.core.local

import android.os.Parcelable
import com.advice.core.local.feedback.ContentFeedbackForm
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Session(
    val id: Long,
    val timeZone: String,
    val start: Instant,
    val end: Instant,
    val location: Location,
    val isBookmarked: Boolean = false,
) : Parcelable {
    val hasStarted: Boolean
        get() = start.compareTo(Instant.now()) == -1

    val hasFinished: Boolean
        get() = end.compareTo(Instant.now()) == -1
}

@Parcelize
data class Event(
    val content: Content,
    val session: Session,
) : Parcelable {

    val id: Long
        get() = session.id

    val conference: String
        get() = content.conference

    val title: String
        get() = content.title

    val description: String
        get() = content.description

    val updated: Instant
        get() = content.updated

    val speakers: List<Speaker>
        get() = content.speakers

    val types: List<Tag>
        get() = content.types

    val urls: List<Action>
        get() = content.urls

    val hasStarted: Boolean
        get() = session.hasStarted

    val hasFinished: Boolean
        get() = session.hasFinished
}

@Parcelize
data class Content(
    val id: Long = -1,
    val conference: String,
    val title: String,
    val description: String,
    val updated: Instant,
    val speakers: List<Speaker>,
    val types: List<Tag>,
    val urls: List<Action>,
    val media: List<OrganizationMedia>,
    var isBookmarked: Boolean = false,
    val sessions: List<Session>,
    val feedback: ContentFeedbackForm? = null,
) : Parcelable
