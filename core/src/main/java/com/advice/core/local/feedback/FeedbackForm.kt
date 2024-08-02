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
) : Parcelable {

    val hasUserData: Boolean
        get() = items.any {
            when (it.type) {
                FeedbackType.DisplayOnly -> false
                is FeedbackType.MultiSelect -> it.type.selections.isNotEmpty()
                is FeedbackType.SelectOne -> it.type.selection != null
                is FeedbackType.TextBox -> it.type.value.isNotBlank()
            }
        }
}
