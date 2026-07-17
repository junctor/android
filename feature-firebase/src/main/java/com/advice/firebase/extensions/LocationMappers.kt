package com.advice.firebase.extensions

import com.advice.core.local.Location
import com.advice.core.local.LocationSchedule
import com.advice.firebase.models.location.FirebaseLocation
import com.advice.firebase.models.location.FirebaseLocationSchedule
import timber.log.Timber
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun FirebaseLocation.toLocation(children: List<Location> = emptyList()): Location? =
    try {
        Location(
            id,
            name,
            shortName,
            defaultStatus,
            hierDepth,
            hierExtentLeft,
            hierExtentRight,
            parentId,
            peerSortOrder,
            schedule?.mapNotNull { it.toSchedule() },
            children,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Location: ${ex.message}")
        null
    }

fun FirebaseLocationSchedule.toSchedule(): LocationSchedule? =
    try {
        val start = runCatching { OffsetDateTime.parse(begin).toInstant() }
            .getOrElse { LocalDateTime.parse(begin).atZone(ZoneId.systemDefault()).toInstant() }
        val end = runCatching { OffsetDateTime.parse(end).toInstant() }
            .getOrElse { LocalDateTime.parse(end).atZone(ZoneId.systemDefault()).toInstant() }
        LocationSchedule(
            start,
            end,
            notes,
            status,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to LocationSchedule: ${ex.message}")
        null
    }
