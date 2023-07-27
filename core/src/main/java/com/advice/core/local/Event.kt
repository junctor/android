package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Event(
    val id: Long = -1,
    val conference: String,
    val timeZone: String,
    val title: String,
    val description: String,
    val start: Instant,
    val end: Instant,
    val updated: Instant,
    val speakers: List<Speaker>,
    val types: List<Tag>,
    val location: Location,
    val urls: List<Action>,
    var isBookmarked: Boolean = false,
) : Parcelable {

    val hasStarted: Boolean
        get() = start.compareTo(Instant.now()) == 1

    val hasFinished: Boolean
        get() = end.compareTo(Instant.now()) == -1
}
