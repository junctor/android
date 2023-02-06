package com.advice.core.local

import android.os.Parcelable
import com.advice.core.utils.Time
import com.advice.schedule.models.firebase.FirebaseMap
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Conference(
        val id: Long,
        val name: String,
        val description: String,
        val conduct: String?,
        val support: String?,
        val code: String,
        val maps: ArrayList<FirebaseMap>,
        val startDate: Date,
        val endDate: Date,
        val timezone: String,
        var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = Time.now().after(endDate)
}