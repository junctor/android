package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Speaker(
    val id: Long,
    val name: String,
    val title: String,
    val description: String,
    val affiliations: List<Affiliation>,
    val links: List<Link>,
) : Parcelable