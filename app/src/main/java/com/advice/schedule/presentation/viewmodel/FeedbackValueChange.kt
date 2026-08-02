package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType

internal fun applyFeedbackValueChange(
    items: List<FeedbackItem>,
    item: FeedbackItem,
    value: String,
): List<FeedbackItem> =
    items.map { feedbackItem ->
        if (feedbackItem.id != item.id) {
            return@map feedbackItem
        }
        when (val type = feedbackItem.type) {
            FeedbackType.DisplayOnly -> feedbackItem
            is FeedbackType.MultiSelect -> {
                val selections =
                    if (value.toLong() in type.selections) {
                        type.selections.filter { it != value.toLong() }
                    } else {
                        type.selections + value.toLong()
                    }
                feedbackItem.copy(type = FeedbackType.MultiSelect(type.options, selections))
            }
            is FeedbackType.SelectOne -> {
                feedbackItem.copy(type = FeedbackType.SelectOne(type.options, value.toLongOrNull()))
            }
            is FeedbackType.TextBox -> {
                feedbackItem.copy(type = FeedbackType.TextBox(value))
            }
        }
    }
