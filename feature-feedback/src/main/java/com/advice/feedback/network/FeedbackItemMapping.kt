package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.feedback.network.models.Feedback

internal fun FeedbackItem.toFeedback(): Feedback? =
    when (val feedbackType = type) {
        FeedbackType.DisplayOnly -> null
        is FeedbackType.MultiSelect -> {
            Feedback(
                itemId = id,
                options = feedbackType.selections.map { it },
            )
        }
        is FeedbackType.SelectOne -> {
            Feedback(
                itemId = id,
                options = listOfNotNull(feedbackType.selection),
            )
        }
        is FeedbackType.TextBox -> {
            Feedback(
                itemId = id,
                text = feedbackType.value,
            )
        }
    }
