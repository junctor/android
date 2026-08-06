package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.schedule.data.repositories.FiltersRepository
import com.advice.ui.states.FiltersScreenState
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

@OptIn(ExperimentalCoroutinesApi::class)
class FiltersViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repositoryState =
        MutableSharedFlow<FiltersScreenState>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<FiltersRepository>(relaxed = true) {
            every { state } returns repositoryState
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state emits loading first`() =
        runTest {
            val viewModel = FiltersViewModel(repository)

            assertTrue(viewModel.state.first() is FiltersScreenState.Loading)
        }

    @Test
    fun `state passes through repository state`() =
        runTest {
            val filters =
                listOf(
                    TagType(1, "Type", "content", true, 0, listOf(tag())),
                )
            repositoryState.emit(FiltersScreenState.Success(filters, isBookmarkSelected = true))
            val viewModel = FiltersViewModel(repository)

            val state = viewModel.state.first { it !is FiltersScreenState.Loading }

            assertTrue(state is FiltersScreenState.Success)
            state as FiltersScreenState.Success
            assertEquals(filters, state.filters)
            assertTrue(state.isBookmarkSelected)
        }

    @Test
    fun `toggle delegates to repository`() =
        runTest {
            val subject = tag()

            FiltersViewModel(repository).toggle(subject)
            advanceUntilIdle()

            coVerify { repository.toggle(subject) }
        }

    @Test
    fun `clear filters delegates to repository`() =
        runTest {
            FiltersViewModel(repository).clearFilters()
            advanceUntilIdle()

            coVerify { repository.clearFilters() }
        }

    private fun tag(): Tag = Tag(10, "Talk", "", "#FF0000", 0)
}
