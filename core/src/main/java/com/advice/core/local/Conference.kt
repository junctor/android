package com.advice.core.local

import android.os.Parcelable
import com.advice.core.utils.Time
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Conference(
    val id: Long,
    val name: String,
    val tagline: String?,
    val code: String,
    val maps: List<ConferenceMap>,
    val kickoffDate: Date,
    val startDate: Date,
    val endDate: Date,
    val timezone: String,
    val flags: Map<String, Boolean>,
    var isSelected: Boolean = false
) : Parcelable {

    val hasFinished: Boolean
        get() = Time.now().after(endDate)

    companion object {
        val Zero = Conference(
            -1,
            "CACTUSCON2023",
            "",
            "CACTUSCON2023",
            ArrayList(),
            Date(),
            Date(),
            Date(),
            "",
            emptyMap(),
            false
        )
    }
}

