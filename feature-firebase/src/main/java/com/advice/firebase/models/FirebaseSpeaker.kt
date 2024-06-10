package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeaker(
    val id: Long = -1,
    val name: String = "",
    val pronouns: String? = null,
    val description: String = "",
    val affiliations: List<FirebaseAffiliation> = emptyList(),
    val links: List<FirebaseSpeakerLink> = emptyList(),
    val conference: String? = null,
    val updated_at: String? = null,
    val hidden: Boolean = false,
) : Parcelable
