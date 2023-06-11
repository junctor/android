package com.advice.retrofit.datasource

import com.advice.core.local.Article
import com.advice.data.session.UserSession
import com.advice.data.sources.ArticleDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RetrofitArticleDataSource(
    private val userSession: UserSession,
    private val retrofitClient: RetrofitClient,
) : ArticleDataSource {

    override fun get(): Flow<List<Article>> {
        return userSession.getConference().map { conference ->
            retrofitClient.get(conference.code).documents.map {
                it.fields.toArticle()
            }
        }
    }
}

private fun com.advice.retrofit.datasource.Article.toArticle(): com.advice.core.local.Article {
    return Article(
        -1,
        name.stringValue,
        text.stringValue,
    )
}
