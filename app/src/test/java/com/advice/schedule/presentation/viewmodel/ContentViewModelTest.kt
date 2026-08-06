package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.FlowResult
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.ui.states.ContentScreenState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ContentViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val contentFlow =
        MutableSharedFlow<FlowResult<ConferenceContent>>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<ContentRepository> {
            every { content } returns contentFlow
        }
    private val bookmarkUseCase = mockk<ContentBookmarkUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is loading until content arrives`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is ContentScreenState.Loading)
        }

    @Test
    fun `content success maps to success state`() =
        runTest {
            val viewModel = createViewModel()
            val items = listOf(content(1), content(2))

            contentFlow.emit(FlowResult.Success(ConferenceContent(items)))
            advanceUntilIdle()

            val state = viewModel.state.first { it !is ContentScreenState.Loading }
            assertTrue(state is ContentScreenState.Success)
            assertEquals(items, (state as ContentScreenState.Success).content)
        }

    @Test
    fun `content failure maps to error state`() =
        runTest {
            val viewModel = createViewModel()

            contentFlow.emit(FlowResult.Failure(IllegalStateException("boom")))
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is ContentScreenState.Error)
        }

    @Test
    fun `bookmark delegates to use case without a session`() =
        runTest {
            val viewModel = createViewModel()
            val subject = content(1)

            viewModel.bookmark(subject, isBookmarked = true)
            advanceUntilIdle()

            coVerify { bookmarkUseCase.bookmark(subject, session = null, isBookmarked = true) }
        }

    private fun createViewModel(): ContentViewModel = ContentViewModel(repository, bookmarkUseCase)

    private fun content(id: Long): Content =
        Content(
            id = id,
            conference = "TEST",
            title = "Content $id",
            description = "",
            updated = Instant.parse("2024-08-01T00:00:00Z"),
            speakers = emptyList(),
            types = emptyList(),
            urls = emptyList(),
            media = emptyList(),
            sessions = emptyList(),
        )
}
