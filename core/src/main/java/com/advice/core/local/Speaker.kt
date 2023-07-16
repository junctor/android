package com.advice.core.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Speaker(
    val id: Long,
    val name: String,
    val title: String,
    val description: String,
    val links: List<Link>,
) : Parcelable