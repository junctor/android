package com.advice.firebase.models.organization

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseOrganizationLocation(
    @get:PropertyName("location_id")
    @set:PropertyName("location_id")
    var locationId: Long = -1L,
) : Parcelable
