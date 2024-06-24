package com.advice.firebase.models.organization

import android.os.Parcelable
import com.advice.firebase.models.FirebaseMedia
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
    val media: List<FirebaseMedia> = emptyList(),
    val tag_id_as_organizer: Long? = null,
    val tag_ids: List<Long> = emptyList(),
) : Parcelable
