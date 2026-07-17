package com.advice.firebase.extensions

import com.advice.core.local.Affiliation
import com.advice.core.local.Link
import com.advice.core.local.Speaker
import com.advice.firebase.models.FirebaseAffiliation
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.FirebaseSpeakerLink
import timber.log.Timber

fun FirebaseSpeaker.toSpeaker(): Speaker? =
    try {
        Speaker(
            id = id,
            name = name,
            pronouns = pronouns,
            description = description,
            affiliations = affiliations.mapNotNull { it.toAffiliation() },
            links =
                links
                    .sortedBy { it.sortOrder }
                    .mapNotNull { it.toLink() },
            roles = emptyList(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }

fun FirebaseAffiliation.toAffiliation(): Affiliation? =
    try {
        Affiliation(organization, title)
    } catch (ex: Exception) {
        Timber.e("Could not map data to Affiliation: ${ex.message}")
        null
    }

fun FirebaseSpeakerLink.toLink(): Link? =
    try {
        Link(
            title,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
        null
    }
