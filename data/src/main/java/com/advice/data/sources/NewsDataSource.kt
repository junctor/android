package com.advice.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsDataSource {
    fun get(): Flow<FlowResult<List<NewsArticle>>>
}
