package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseConferenceMediaTheme(
    @get:PropertyName("banner_background")
    @set:PropertyName("banner_background")
    var bannerBackground: String? = null,
    @get:PropertyName("banner_logo")
    @set:PropertyName("banner_logo")
    var bannerLogo: String? = null,
    @get:PropertyName("square_logo")
    @set:PropertyName("square_logo")
    var squareLogo: String? = null,
) : Parcelable
