package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackItem(
    val id: Long,
    val caption: String,
    val type: FeedbackType,
) : Parcelable
