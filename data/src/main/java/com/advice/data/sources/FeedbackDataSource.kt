package com.advice.data.sources

import com.advice.core.local.feedback.FeedbackForm
import kotlinx.coroutines.flow.Flow

interface FeedbackDataSource {
    fun get(): Flow<List<FeedbackForm>>
}
