package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackOption(
    val id: Long,
    val value: String,
) : Parcelable

sealed class FeedbackType : Parcelable {
    @Parcelize
    data object DisplayOnly : FeedbackType()

    @Parcelize
    data class SelectOne(val options: List<FeedbackOption>, val selection: Long? = null) :
        FeedbackType()

    @Parcelize
    data class MultiSelect(
        val options: List<FeedbackOption>,
        val selections: List<Long> = emptyList()
    ) : FeedbackType()

    @Parcelize
    data class TextBox(val value: String) : FeedbackType()
}
