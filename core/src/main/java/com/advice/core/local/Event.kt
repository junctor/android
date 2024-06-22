package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Session(
    val id: Long,
    val timeZone: String,
    val start: Instant,
    val end: Instant,
    val location: Location,
) : Parcelable {
    val hasStarted: Boolean
        get() = start.compareTo(Instant.now()) == -1

    val hasFinished: Boolean
        get() = end.compareTo(Instant.now()) == -1
}

@Parcelize
data class Event(
    val id: Long = -1,
    val conference: String,
    val title: String,
    val description: String,
    val session: Session,
    val updated: Instant,
    val speakers: List<Speaker>,
    val types: List<Tag>,
    val urls: List<Action>,
    var isBookmarked: Boolean = false,
) : Parcelable {

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
    var isBookmarked: Boolean = false,
    val sessions: List<Session>,
) : Parcelable
