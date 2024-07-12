package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackForm(
    val id: Long,
    val title: String,
    val items: List<FeedbackItem>,
): Parcelable
