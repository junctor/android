package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Speaker(
    val id: Long,
    val name: String,
    val pronouns: String?,
    val description: String,
    val affiliations: List<Affiliation>,
    val links: List<Link>,
    val roles: List<Tag>,
) : Parcelable {
    val title: String?
        get() = affiliations.firstOrNull()?.organization
}
