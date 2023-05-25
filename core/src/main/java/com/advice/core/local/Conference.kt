package com.advice.core.local

import android.os.Parcelable
import com.advice.core.utils.Time
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Conference(
    val id: Long,
    val name: String,
    val description: String,
    val conduct: String?,
    val support: String?,
    val code: String,
    val maps: List<ConferenceMap>,
    val kickoffDate: Date,
    val startDate: Date,
    val endDate: Date,
    val timezone: String,
    var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = Time.now().after(endDate)

    companion object {
        val Zero = Conference(-1, "CACTUSCON2023", "", null, null, "CACTUSCON2023", ArrayList(), Date(), Date(), Date(), "", false)
    }
}

