package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.ArticleDataSource

class HomeRepository(private val articleDataSource: ArticleDataSource) {

    val contents = articleDataSource.get()
}