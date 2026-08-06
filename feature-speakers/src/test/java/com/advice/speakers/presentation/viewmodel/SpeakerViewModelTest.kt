package com.advice.speakers.presentation.viewmodel

import com.advice.core.local.Speaker
import com.advice.speakers.data.repositories.SpeakerRepository
import com.advice.ui.states.SpeakerState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SpeakerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<SpeakerRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `null id emits error state`() =
        runTest {
            val viewModel = SpeakerViewModel(repository)

            viewModel.fetchSpeakerDetails(id = null)
            advanceUntilIdle()

            assertTrue(viewModel.speakerDetails.first() is SpeakerState.Error)
        }

    @Test
    fun `details pass through from the repository`() =
        runTest {
            val details = SpeakerState.Success(mockk<Speaker>(), emptyList())
            coEvery { repository.getSpeakerDetails(1L) } returns details
            val viewModel = SpeakerViewModel(repository)

            viewModel.fetchSpeakerDetails(id = 1L)
            advanceUntilIdle()

            assertEquals(details, viewModel.speakerDetails.first())
        }

    @Test
    fun `missing speaker emits error state`() =
        runTest {
            coEvery { repository.getSpeakerDetails(1L) } returns SpeakerState.Error
            val viewModel = SpeakerViewModel(repository)

            viewModel.fetchSpeakerDetails(id = 1L)
            advanceUntilIdle()

            assertTrue(viewModel.speakerDetails.first() is SpeakerState.Error)
        }
}
