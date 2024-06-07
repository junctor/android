package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.Math.max
import java.time.Instant

@Parcelize
data class Location(
    val id: Long,
    val name: String,
    val shortName: String?,
    val conference: String,
    // Schedule
    val defaultStatus: String? = null,
    val depth: Int = -1,
    val hierExtentLeft: Int = -1,
    val hierExtentRight: Int = -1,
    val parent: Long = -1,
    val peerSortOrder: Int = -1,
    val schedule: List<LocationSchedule>? = null,
    val children: List<Location> = emptyList(),
    var isVisible: Boolean = true,
    var isExpanded: Boolean = false,
) : Parcelable {

    val hasChildren: Boolean
        get() = children.isNotEmpty()

    val status: LocationStatus
        get() {
            if (!hasChildren) {
                return getCurrentStatus()
            }

            val childrenStatus = children.map { it.status }
            return when {
                childrenStatus.all { it == LocationStatus.Open } -> LocationStatus.Open
                childrenStatus.all { it == LocationStatus.Closed } -> LocationStatus.Closed
                childrenStatus.all { it == LocationStatus.Unknown } -> LocationStatus.Unknown
                else -> LocationStatus.Mixed
            }
        }

    private fun getCurrentStatus(): LocationStatus {
        val now = Instant.now()

        val status = schedule?.firstOrNull {
            it.start.compareTo(now) == -1 && it.end.compareTo(now) == 1
        }?.status ?: defaultStatus

        return when (status) {
            "open" -> LocationStatus.Open
            "closed" -> LocationStatus.Closed
            else -> LocationStatus.Unknown
        }
    }
}
