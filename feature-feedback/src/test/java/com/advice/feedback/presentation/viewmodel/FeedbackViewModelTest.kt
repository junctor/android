package com.advice.feedback.presentation.viewmodel

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.utils.ToastManager
import com.advice.data.network.NetworkResponse
import com.advice.feedback.data.repositories.FeedbackFormRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.ui.screens.FeedbackState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val formRepository = mockk<FeedbackFormRepository>()
    private val submissionRepository = mockk<FeedbackSubmissionRepository>()
    private val toastManager = ToastManager()

    private val form =
        FeedbackForm(
            id = 10,
            conference = 1,
            title = "Session Feedback",
            items = emptyList(),
            endpoint = "https://example.com/feedback",
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetching a known form emits content state`() =
        runTest {
            coEvery { formRepository.getFeedbackForm(10L) } returns form
            val viewModel = createViewModel()

            viewModel.fetchFeedbackForm(10L)
            advanceUntilIdle()

            val state = viewModel.state.first()
            assertTrue(state is FeedbackState.Content)
            assertEquals(form, (state as FeedbackState.Content).feedback)
        }

    @Test
    fun `fetching an unknown form emits error state`() =
        runTest {
            coEvery { formRepository.getFeedbackForm(99L) } returns null
            val viewModel = createViewModel()

            viewModel.fetchFeedbackForm(99L)
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is FeedbackState.Error)
        }

    @Test
    fun `successful submission completes without error`() =
        runTest {
            coEvery { submissionRepository.submitFeedback(null, form) } returns NetworkResponse.Success
            val viewModel = loadedViewModel()

            viewModel.submitFeedback(content = null)
            advanceUntilIdle()

            val state = viewModel.state.first() as FeedbackState.Content
            assertTrue(state.isComplete)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            assertNull(toastManager.messages.first())
        }

    @Test
    fun `failed submission sets error message and pushes a toast`() =
        runTest {
            coEvery { submissionRepository.submitFeedback(null, form) } returns
                NetworkResponse.Error(IllegalStateException("offline"))
            val viewModel = loadedViewModel()

            viewModel.submitFeedback(content = null)
            advanceUntilIdle()

            val state = viewModel.state.first() as FeedbackState.Content
            assertTrue(state.isComplete)
            assertEquals("Could not submit feedback: offline", state.errorMessage)
            assertEquals("Could not submit feedback: offline", toastManager.messages.first()?.text)
        }

    @Test
    fun `back press shows discard popup until cancelled`() =
        runTest {
            val viewModel = loadedViewModel()

            viewModel.onBackPressed()
            assertTrue((viewModel.state.first() as FeedbackState.Content).showingDiscardPopup)

            viewModel.onDiscardPopupCancelled()
            assertFalse((viewModel.state.first() as FeedbackState.Content).showingDiscardPopup)
        }

    private fun createViewModel(): FeedbackViewModel = FeedbackViewModel(formRepository, submissionRepository, toastManager)

    private fun TestScope.loadedViewModel(): FeedbackViewModel {
        coEvery { formRepository.getFeedbackForm(10L) } returns form
        val viewModel = createViewModel()
        viewModel.fetchFeedbackForm(10L)
        advanceUntilIdle()
        return viewModel
    }
}
