package com.advice.retrofit.datasource

import com.advice.core.local.NewsArticle

private fun Article.toArticle(): NewsArticle =
    NewsArticle(
        -1,
        name.stringValue,
        text.stringValue,
        null,
    )
