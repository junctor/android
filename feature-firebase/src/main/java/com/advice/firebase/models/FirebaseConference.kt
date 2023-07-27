package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class FirebaseConference(
    val id: Long = 0,
    val name: String = "",
    val tagline_text: String? = null,
    val codeofconduct: String? = null,
    val supportdoc: String? = null,
    val code: String = "",
    val maps: ArrayList<FirebaseMap> = ArrayList(),
    val start_date: String = "",
    val end_date: String = "",
    val begin_timestamp: Timestamp? = null,
    val kickoff_timestamp: Timestamp = Timestamp(Date()),
    val start_timestamp: Timestamp = Timestamp(Date()),
    val end_timestamp: Timestamp = Timestamp(Date()),
    val timezone: String = "",
    val hidden: Boolean = false,
    val developer: Boolean = false,
    val enable_merch: Boolean = false,
    val enable_merch_cart: Boolean = false,
    val enable_wifi: Boolean = false,
) : Parcelable
