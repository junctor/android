package com.advice.schedule.data.repositories

import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.sources.FeedbackDataSource
import kotlinx.coroutines.flow.first

class FeedbackRepository(
    private val feedbackDataSource: FeedbackDataSource,
) {
    suspend fun getFeedbackForm(id: Long): FeedbackForm? {
        val forms = feedbackDataSource.get().first()
        return forms.find { it.id == id }
    }
}
