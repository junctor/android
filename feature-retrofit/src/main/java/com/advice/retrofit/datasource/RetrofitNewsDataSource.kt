package com.advice.retrofit.datasource

import com.advice.core.local.NewsArticle
import com.advice.data.session.UserSession
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RetrofitNewsDataSource(
    private val userSession: UserSession,
    private val retrofitClient: RetrofitClient,
) : NewsDataSource {

    override fun get(): Flow<List<NewsArticle>> {
        return userSession.getConference().map { conference ->
            retrofitClient.get(conference.code).documents.map {
                it.fields.toArticle()
            }
        }
    }
}

private fun com.advice.retrofit.datasource.Article.toArticle(): com.advice.core.local.NewsArticle {
    return NewsArticle(
        -1,
        name.stringValue,
        text.stringValue,
        null,
    )
}
