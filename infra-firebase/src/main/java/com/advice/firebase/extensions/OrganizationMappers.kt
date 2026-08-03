package com.advice.firebase.extensions

import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.core.local.OrganizationLocation
import com.advice.core.local.OrganizationMedia
import com.advice.firebase.models.FirebaseMedia
import com.advice.firebase.models.organization.FirebaseLink
import com.advice.firebase.models.organization.FirebaseOrganization
import com.advice.firebase.models.organization.FirebaseOrganizationLocation
import timber.log.Timber

fun FirebaseOrganization.toOrganization(): Organization? =
    try {
        Organization(
            id,
            name,
            description,
            locations.mapNotNull { it.toLocation() },
            links.mapNotNull { it.toLink() },
            media.mapNotNull { it.toMedia() },
            tagIdAsOrganizer,
            tagIds,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Organization: ${ex.message}")
        null
    }

fun FirebaseOrganizationLocation.toLocation(): OrganizationLocation? =
    try {
        OrganizationLocation(
            locationId,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to OrganizationLocation: ${ex.message}")
        null
    }

fun FirebaseLink.toLink(): OrganizationLink? =
    try {
        OrganizationLink(
            label,
            type,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
        null
    }

fun FirebaseMedia.toMedia(): OrganizationMedia? =
    try {
        OrganizationMedia(
            assetId,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Media: ${ex.message}")
        null
    }
