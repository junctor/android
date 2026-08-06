package com.advice.feedback.presentation.viewmodel

import com.advice.core.local.Conference
import com.advice.core.utils.ToastManager
import com.advice.data.network.NetworkResponse
import com.advice.data.session.UserSession
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.feedback.network.models.ReportObjectType
import com.advice.ui.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
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
class ReportViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val reportRepository = mockk<ReportSubmissionRepository>()
    private val userSession = mockk<UserSession>()
    private val toastManager = ToastManager()

    private val conference = Conference.Zero

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful submission pushes success toast`() =
        runTest {
            every { userSession.currentConference } returns conference
            coEvery {
                reportRepository.submit("broken link", ReportObjectType.CONTENT, 1L, conference)
            } returns NetworkResponse.Success
            val viewModel = createViewModel()

            viewModel.submit("broken link", ReportObjectType.CONTENT, 1L)
            advanceUntilIdle()

            assertEquals(R.string.report_success, toastManager.messages.first()?.resId)
        }

    @Test
    fun `failed submission pushes error toast`() =
        runTest {
            every { userSession.currentConference } returns conference
            coEvery {
                reportRepository.submit("broken link", ReportObjectType.CONTENT, 1L, conference)
            } returns NetworkResponse.Error(IllegalStateException("offline"))
            val viewModel = createViewModel()

            viewModel.submit("broken link", ReportObjectType.CONTENT, 1L)
            advanceUntilIdle()

            assertEquals(R.string.report_error, toastManager.messages.first()?.resId)
        }

    @Test
    fun `missing conference pushes error toast without submitting`() =
        runTest {
            every { userSession.currentConference } returns null
            val viewModel = createViewModel()

            viewModel.submit("broken link", ReportObjectType.CONTENT, 1L)
            advanceUntilIdle()

            assertEquals(R.string.report_error, toastManager.messages.first()?.resId)
            coVerify(exactly = 0) { reportRepository.submit(any(), any(), any(), any()) }
        }

    private fun createViewModel(): ReportViewModel = ReportViewModel(reportRepository, userSession, toastManager)
}
