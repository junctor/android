package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseVendor(
    val id: Int = -1,
    val name: String = "",
    val description: String? = null,
    val link: String? = null,
    val partner: Boolean = false,
    val updatedAt: String = "",
    val conference: String = "",
    val hidden: Boolean = false
) : Parcelable
