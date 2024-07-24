package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class FirebaseConference(
    val begin_tsz: String = "",
    val code: String = "",
    @Deprecated("Use documents collection instead.")
    val codeofconduct: String = "",
    val conference_id: Long = -1,
    val description: String = "",
    val enable_merch: Boolean = false,
    val enable_merch_cart: Boolean = false,
    val enable_wifi: Boolean = false,
    val end_date: String = "",
    val end_timestamp: Timestamp = Timestamp(Date()),
    val end_timestamp_str: String = "",
    val end_tsz: String = "",
    val feedbackform_ratelimit_seconds: Int = 60,
    val hidden: Boolean = false,
    val home_menu_id: Long? = null,
    val id: Long = 0,
    val kickoff_timestamp: Timestamp = Timestamp(Date()),
    val kickoff_timestamp_tsz: String = "",
    val kickoff_tsz: String = "",
    @Deprecated("")
    val link: String = "",
    val maps: ArrayList<FirebaseMap> = ArrayList(),
    val merch_mandatory_acknowledgement: String? = null,
    val merch_tax_statement: String? = null,
    val name: String = "",
    val start_date: String = "",
    val start_timestamp: Timestamp = Timestamp(Date()),
    val start_timestamp_str: String = "",
    @Deprecated("Use documents collection instead.")
    val suppordoc: String = "",
    val tagline_text: String? = null,
    val merch_help_doc_id: Long? = null,
    val timezone: String = "",
    val developer: Boolean = false,
) : Parcelable
