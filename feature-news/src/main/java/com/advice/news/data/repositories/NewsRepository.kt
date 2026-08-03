package com.advice.news.data.repositories

import com.advice.core.local.FlowResult
import com.advice.core.local.NewsArticle
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val newsDataSource: NewsDataSource,
) {
    fun get(): Flow<FlowResult<List<NewsArticle>>> = newsDataSource.get()
}
