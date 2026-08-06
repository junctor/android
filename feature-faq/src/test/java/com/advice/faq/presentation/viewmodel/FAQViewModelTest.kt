package com.advice.faq.presentation.viewmodel

import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import com.advice.data.sources.FAQDataSource
import com.advice.faq.data.repositories.FAQRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FAQViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val faqsFlow =
        MutableSharedFlow<FlowResult<List<FAQ>>>(replay = 1, extraBufferCapacity = 16)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is loading until faqs arrive`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is FAQScreenState.Loading)
        }

    @Test
    fun `success maps to success state with faqs`() =
        runTest {
            val viewModel = createViewModel()
            val faqs = listOf(FAQ("What is this?", "A conference app"))

            faqsFlow.emit(FlowResult.Success(faqs))
            advanceUntilIdle()

            val state = viewModel.state.first { it !is FAQScreenState.Loading }
            assertTrue(state is FAQScreenState.Success)
            assertEquals(faqs, (state as FAQScreenState.Success).faqs)
        }

    @Test
    fun `failure maps to error state with exception`() =
        runTest {
            val viewModel = createViewModel()
            val exception = IllegalStateException("boom")

            faqsFlow.emit(FlowResult.Failure(exception))
            advanceUntilIdle()

            val state = viewModel.state.first { it !is FAQScreenState.Loading }
            assertTrue(state is FAQScreenState.Error)
            assertEquals(exception, (state as FAQScreenState.Error).error)
        }

    private fun createViewModel(): FAQViewModel {
        val dataSource =
            object : FAQDataSource {
                override fun get() = faqsFlow
            }
        return FAQViewModel(FAQRepository(dataSource))
    }
}
