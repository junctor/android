package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceMap(
    val name: String = "",
    val file: String = "",
    val description: String? = null
) : Parcelable