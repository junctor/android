package com.advice.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.feedback.FeedbackForm
import kotlinx.coroutines.flow.Flow

interface FeedbackDataSource {
    fun get(): Flow<FlowResult<List<FeedbackForm>>>
    suspend fun fetch(conference: String): List<FeedbackForm>
}
