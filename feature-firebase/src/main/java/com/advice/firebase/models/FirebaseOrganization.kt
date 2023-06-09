package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class FirebaseOrganization(
    val conference_id: Long = -1L,
    val conference: String = "",
    val id: Long = -1L,
    val name: String = "",
    val description: String? = null,
    val links: List<FirebaseLink> = emptyList(),
    val locations: List<FirebaseOrganizationLocation> = emptyList(),
    val media: List<FirebaseOrganizationMedia> = emptyList(),
    val tag_ids: List<Long> = emptyList(),
) : Parcelable

@Parcelize
data class FirebaseOrganizationLocation(
    val location_id: Long = -1L,
) : Parcelable

@Parcelize
data class FirebaseLink(
    val label: String = "",
    val type: String = "link",
    val url: String = "",
) : Parcelable

@Parcelize
data class FirebaseOrganizationMedia(
    val asset_id: Long = -1L,
    val url: String = "",
): Parcelable