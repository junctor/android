package com.advice.firebase.models.organization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseOrganizationLocation(
    val location_id: Long = -1L,
) : Parcelable
