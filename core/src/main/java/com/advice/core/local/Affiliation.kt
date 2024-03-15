package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Affiliation(
    val organization: String,
    val title: String,
) : Parcelable
