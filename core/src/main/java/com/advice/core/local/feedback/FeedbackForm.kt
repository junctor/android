package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackForm(
    val id: Long,
    val conference: Long,
    val title: String,
    val items: List<FeedbackItem>,
    val endpoint: String,
): Parcelable
