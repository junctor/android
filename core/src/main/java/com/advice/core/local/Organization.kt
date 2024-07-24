package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organization(
    val id: Long,
    val name: String,
    val description: String?,
    val locations: List<OrganizationLocation>,
    val links: List<OrganizationLink>,
    val media: List<OrganizationMedia>,
    val tag: Long?,
    val tags: List<Long>,
) : Parcelable

@Parcelize
data class OrganizationLocation(
    val id: Long,
) : Parcelable

@Parcelize
data class OrganizationLink(
    val label: String,
    val type: String,
    val url: String,
) : Parcelable

@Parcelize
data class OrganizationMedia(
    val id: Long,
    val url: String,
) : Parcelable
