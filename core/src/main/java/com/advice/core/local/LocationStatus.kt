package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class LocationStatus : Parcelable {
    object Open : LocationStatus()
    object Closed : LocationStatus()
    object Mixed : LocationStatus()
    object Unknown : LocationStatus()
}