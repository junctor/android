package com.advice.data.sources

import com.advice.core.local.Article
import kotlinx.coroutines.flow.Flow

interface ArticleDataSource {

    fun get(): Flow<List<Article>>
}