package com.advice.firebase.extensions

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackOption
import com.advice.core.local.feedback.FeedbackType
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.advice.firebase.models.feedback.FirebaseFeedbackItem
import timber.log.Timber

fun FirebaseFeedbackForm.toFeedbackForm(): FeedbackForm? =
    try {
        FeedbackForm(
            id = id,
            conference = conferenceId,
            title = nameText,
            items = items.sortedBy { it.sortOrder }.mapNotNull { it.toFeedbackItem() },
            endpoint = submissionUrl,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to FeedbackForm: ${ex.message}")
        null
    }

fun FirebaseFeedbackItem.toFeedbackItem(): FeedbackItem? =
    try {
        val type = when (type) {
            "display_only" -> FeedbackType.DisplayOnly
            "select_one" -> FeedbackType.SelectOne(
                options.sortedBy { it.sortOrder }.map { FeedbackOption(it.id, it.captionText) }
            )

            "multi_select" -> FeedbackType.MultiSelect(
                options.sortedBy { it.sortOrder }.map { FeedbackOption(it.id, it.captionText) }
            )

            "text" -> FeedbackType.TextBox("")
            else -> error("Unknown feedback type: $type")
        }
        FeedbackItem(
            id = id,
            caption = captionText,
            type = type,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to FeedbackItem: ${ex.message}")
        null
    }
