package com.advice.news.presentation.viewmodel

import com.advice.core.local.FlowResult
import com.advice.core.local.NewsArticle
import com.advice.data.sources.NewsDataSource
import com.advice.news.data.repositories.NewsRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NewsViewModelTest {
    @Test
    fun `news passes through from the data source`() =
        runTest {
            val articles = listOf(NewsArticle(1, "Update", "Text", date = null))
            val dataSource =
                object : NewsDataSource {
                    override fun get() = flowOf<FlowResult<List<NewsArticle>>>(FlowResult.Success(articles))
                }
            val viewModel = NewsViewModel(NewsRepository(dataSource))

            val result = viewModel.getNews().first()

            assertTrue(result is FlowResult.Success)
            assertEquals(articles, (result as FlowResult.Success).value)
        }
}
