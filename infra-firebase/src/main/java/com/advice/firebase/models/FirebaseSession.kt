package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSession(
    @get:PropertyName("begin_timestamp")
    @set:PropertyName("begin_timestamp")
    var beginTimestamp: Timestamp = Timestamp.now(),
    @get:PropertyName("begin_tsz")
    @set:PropertyName("begin_tsz")
    var beginTsz: String = "",
    @get:PropertyName("channel_id")
    @set:PropertyName("channel_id")
    var channelId: Long? = null,
    @get:PropertyName("end_timestamp")
    @set:PropertyName("end_timestamp")
    var endTimestamp: Timestamp = Timestamp.now(),
    @get:PropertyName("end_tsz")
    @set:PropertyName("end_tsz")
    var endTsz: String = "",
    @get:PropertyName("location_id")
    @set:PropertyName("location_id")
    var locationId: Long = -1,
    @get:PropertyName("recordingpolicy_id")
    @set:PropertyName("recordingpolicy_id")
    var recordingPolicyId: Long? = null,
    @get:PropertyName("session_id")
    @set:PropertyName("session_id")
    var sessionId: Long = -1,
    @get:PropertyName("timezone_name")
    @set:PropertyName("timezone_name")
    var timezoneName: String = "",
) : Parcelable
