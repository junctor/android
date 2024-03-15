package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vendor(
    val id: Int,
    val name: String,
    val summary: String?,
    val link: String?,
    val partner: Boolean,
    val image: String? = null,
) : Parcelable
