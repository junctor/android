package com.advice.core.local.products

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductMedia(
    val url: String,
    val sortOrder: Int,
) : Parcelable