package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Conference(
    val id: Long,
    val name: String,
    val tagline: String?,
    val code: String,
    val homeMenuId: Long?,
    val merchDocumentId: Long?,
    val maps: List<ConferenceMap>,
    val kickoffDate: Instant,
    val start: Instant,
    val end: Instant,
    val timezone: String,
    val flags: Map<String, Boolean>,
    var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = end.compareTo(Instant.now()) == -1

    companion object {
        val Zero = Conference(
            id = -1,
            name = "TEST 2023",
            tagline = "",
            code = "TEST2023",
            homeMenuId = null,
            merchDocumentId = null,
            maps = ArrayList(),
            kickoffDate = Instant.now(),
            start = Instant.now(),
            end = Instant.now(),
            timezone = "",
            flags = emptyMap(),
            isSelected = false
        )
    }
}
