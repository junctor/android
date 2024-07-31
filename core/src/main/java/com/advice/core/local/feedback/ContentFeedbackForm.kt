package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class ContentFeedbackForm(
    val enable: Instant?,
    val disable: Instant?,
    val form: FeedbackForm,
) : Parcelable {
    val title: String
        get() = form.title

    val isEnabled: Boolean
        get() {
            return (enable == null || enable.isBefore(Instant.now())) &&
                (disable == null || disable.isAfter(Instant.now()))
        }
}
