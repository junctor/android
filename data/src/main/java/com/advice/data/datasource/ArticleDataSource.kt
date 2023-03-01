package com.advice.data.datasource

import com.advice.schedule.models.local.Article
import kotlinx.coroutines.flow.Flow

interface ArticleDataSource {

    fun get(): Flow<List<Article>>
}