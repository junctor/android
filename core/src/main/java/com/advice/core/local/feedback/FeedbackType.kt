package com.advice.core.local.feedback

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class FeedbackType : Parcelable {
    @Parcelize
    object DisplayOnly : FeedbackType()

    @Parcelize
    data class SelectOne(val options: List<String>, val selection: String? = null) : FeedbackType()

    @Parcelize
    data class MultiSelect(val options: List<String>, val selections: List<String> = emptyList()) :
        FeedbackType()

    @Parcelize
    data class TextBox(val value: String) : FeedbackType()
}
