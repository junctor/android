package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class FirebaseConference(
    @get:PropertyName("begin_tsz")
    @set:PropertyName("begin_tsz")
    var beginTsz: String = "",
    @get:PropertyName("code")
    @set:PropertyName("code")
    var code: String = "",
    @Deprecated("Use documents collection instead.")
    @get:PropertyName("codeofconduct")
    @set:PropertyName("codeofconduct")
    var codeofconduct: String = "",
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1,
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("enable_merch")
    @set:PropertyName("enable_merch")
    var enableMerch: Boolean = false,
    @get:PropertyName("enable_merch_cart")
    @set:PropertyName("enable_merch_cart")
    var enableMerchCart: Boolean = false,
    @get:PropertyName("enable_wifi")
    @set:PropertyName("enable_wifi")
    var enableWifi: Boolean = false,
    @get:PropertyName("end_date")
    @set:PropertyName("end_date")
    var endDate: String = "",
    @get:PropertyName("end_timestamp")
    @set:PropertyName("end_timestamp")
    var endTimestamp: Timestamp = Timestamp(Date()),
    @get:PropertyName("end_timestamp_str")
    @set:PropertyName("end_timestamp_str")
    var endTimestampStr: String = "",
    @get:PropertyName("end_tsz")
    @set:PropertyName("end_tsz")
    var endTsz: String = "",
    @get:PropertyName("feedbackform_ratelimit_seconds")
    @set:PropertyName("feedbackform_ratelimit_seconds")
    var feedbackformRatelimitSeconds: Int = 60,
    @get:PropertyName("hidden")
    @set:PropertyName("hidden")
    var hidden: Boolean = false,
    @get:PropertyName("home_menu_id")
    @set:PropertyName("home_menu_id")
    var homeMenuId: Long? = null,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = 0,
    @get:PropertyName("kickoff_timestamp")
    @set:PropertyName("kickoff_timestamp")
    var kickoffTimestamp: Timestamp = Timestamp(Date()),
    @get:PropertyName("kickoff_timestamp_str")
    @set:PropertyName("kickoff_timestamp_str")
    var kickoffTimestampStr: String = "",
    @get:PropertyName("kickoff_timestamp_tsz")
    @set:PropertyName("kickoff_timestamp_tsz")
    var kickoffTimestampTsz: String = "",
    @get:PropertyName("kickoff_tsz")
    @set:PropertyName("kickoff_tsz")
    var kickoffTsz: String = "",
    @Deprecated("")
    @get:PropertyName("link")
    @set:PropertyName("link")
    var link: String = "",
    @get:PropertyName("maps")
    @set:PropertyName("maps")
    var maps: ArrayList<FirebaseMap> = ArrayList(),
    @get:PropertyName("merch_mandatory_acknowledgement")
    @set:PropertyName("merch_mandatory_acknowledgement")
    var merchMandatoryAcknowledgement: String? = null,
    @get:PropertyName("merch_tax_statement")
    @set:PropertyName("merch_tax_statement")
    var merchTaxStatement: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("start_date")
    @set:PropertyName("start_date")
    var startDate: String = "",
    @get:PropertyName("start_timestamp")
    @set:PropertyName("start_timestamp")
    var startTimestamp: Timestamp = Timestamp(Date()),
    @get:PropertyName("start_timestamp_str")
    @set:PropertyName("start_timestamp_str")
    var startTimestampStr: String = "",
    @Deprecated("Use documents collection instead.")
    @get:PropertyName("supportdoc")
    @set:PropertyName("supportdoc")
    var supportDoc: String = "",
    @get:PropertyName("tagline_text")
    @set:PropertyName("tagline_text")
    var taglineText: String? = null,
    @get:PropertyName("merch_help_doc_id")
    @set:PropertyName("merch_help_doc_id")
    var merchHelpDocId: Long? = null,
    @get:PropertyName("timezone")
    @set:PropertyName("timezone")
    var timezone: String = "",
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp = Timestamp.now(),
) : Parcelable
