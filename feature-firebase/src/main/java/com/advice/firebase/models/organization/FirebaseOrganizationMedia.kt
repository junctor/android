package com.advice.firebase.models.organization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseOrganizationMedia(
    val asset_id: Long = -1L,
    val url: String = "",
) : Parcelable
