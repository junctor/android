package com.advice.schedule.presentation.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.Document
import com.advice.core.local.FlowResult
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.storage.UserPreferencesStore
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.firebase.telemetry.FirestoreTelemetry
import com.advice.play.AppManager
import com.advice.schedule.offline.OfflineQueueConnectivityMonitor
import com.advice.schedule.ui.components.DragAnchors
import com.shortstack.hackertracker.BuildConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val conferenceFlow = MutableSharedFlow<Conference>(replay = 1, extraBufferCapacity = 16)

    private val userSession =
        mockk<UserSession>(relaxed = true) {
            every { getConference() } returns conferenceFlow
        }
    private val appManager = mockk<AppManager>(relaxed = true)
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val preferences =
        mockk<UserPreferencesStore>(relaxed = true) {
            every { showSchedule } returns false
            every { updateVersion } returns -1
        }
    private val documentsRepository = mockk<DocumentsRepository>()
    private val feedbackRepository = mockk<FeedbackSubmissionRepository>(relaxed = true)
    private val reportRepository = mockk<ReportSubmissionRepository>(relaxed = true)
    private val conferencesDataSource = mockk<ConferencesDataSource>()
    private val connectivityMonitor = mockk<OfflineQueueConnectivityMonitor>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts on home anchor with bottom nav shown by default`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.state.first()
            assertEquals(DragAnchors.Start, state.currentAnchor)
            assertTrue(state.isShown)
        }

    @Test
    fun `starts on schedule anchor when preference is enabled`() =
        runTest {
            every { preferences.showSchedule } returns true

            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.state.first()
            assertEquals(DragAnchors.Center, state.currentAnchor)
            assertFalse(state.isShown)
        }

    @Test
    fun `init drains both cached submission queues`() =
        runTest {
            createViewModel()
            advanceUntilIdle()

            coVerify { feedbackRepository.retryCached() }
            coVerify { reportRepository.retryCached() }
        }

    @Test
    fun `feedback queue drain failure does not prevent report drain`() =
        runTest {
            coEvery { feedbackRepository.retryCached() } throws RuntimeException("offline")

            createViewModel()
            advanceUntilIdle()

            coVerify { reportRepository.retryCached() }
        }

    @Test
    fun `emergency document follows conference changes`() =
        runTest {
            val document = Document(5, "Emergency", "Details")
            coEvery { documentsRepository.get(5) } returns document
            val viewModel = createViewModel()

            conferenceFlow.emit(Conference.Zero.copy(emergencyDocumentId = 5))
            advanceUntilIdle()
            assertEquals(document, viewModel.state.first().emergencyDocument)

            conferenceFlow.emit(Conference.Zero.copy(emergencyDocumentId = null))
            advanceUntilIdle()
            assertNull(viewModel.state.first().emergencyDocument)
        }

    @Test
    fun `switch conference by code returns true when already current`() =
        runTest {
            val current = Conference.Zero.copy(code = "DEFCON33")
            every { userSession.currentConference } returns current
            val viewModel = createViewModel()

            assertTrue(viewModel.switchConferenceByCode("DEFCON33"))
            verify(exactly = 0) { userSession.setConference(any()) }
        }

    @Test
    fun `switch conference by code selects the matching conference`() =
        runTest {
            val target = Conference.Zero.copy(id = 2, code = "DEFCON34")
            every { userSession.currentConference } returns null
            every { conferencesDataSource.get() } returns
                flowOf(FlowResult.Success(listOf(Conference.Zero.copy(id = 1, code = "DEFCON33"), target)))
            val viewModel = createViewModel()

            assertTrue(viewModel.switchConferenceByCode("DEFCON34"))
            verify { userSession.setConference(target) }
        }

    @Test
    fun `switch conference by code returns false when not found`() =
        runTest {
            every { userSession.currentConference } returns null
            every { conferencesDataSource.get() } returns
                flowOf(FlowResult.Success(listOf(Conference.Zero.copy(code = "DEFCON33"))))
            val viewModel = createViewModel()

            assertFalse(viewModel.switchConferenceByCode("UNKNOWN"))
            verify(exactly = 0) { userSession.setConference(any()) }
        }

    @Test
    fun `permission dialog dismissal persists the preference`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.showPermissionDialog()
            assertTrue(viewModel.state.first().permissionDialog)

            viewModel.dismissPermissionDialog()
            assertFalse(viewModel.state.first().permissionDialog)
            verify { preferences.dismissNotificationPopup() }
        }

    @Test
    fun `app start runs once and starts the connectivity monitor`() =
        runTest {
            val launcher = mockk<ActivityResultLauncher<IntentSenderRequest>>()
            val viewModel = createViewModel()

            assertTrue(viewModel.onAppStart(is24HourFormat = true, appUpdateLauncher = launcher))
            assertFalse(viewModel.onAppStart(is24HourFormat = true, appUpdateLauncher = launcher))

            verify(exactly = 1) { appManager.checkForUpdate(launcher) }
            verify(exactly = 1) { connectivityMonitor.start() }
            verify { analytics.setUserProperty("time_format", "24h") }
        }

    @Test
    fun `app start skips update check when version was already prompted`() =
        runTest {
            every { preferences.updateVersion } returns BuildConfig.VERSION_CODE
            val launcher = mockk<ActivityResultLauncher<IntentSenderRequest>>()
            val viewModel = createViewModel()

            assertTrue(viewModel.onAppStart(is24HourFormat = false, appUpdateLauncher = launcher))

            verify(exactly = 0) { appManager.checkForUpdate(any()) }
            verify { analytics.setUserProperty("time_format", "12h") }
        }

    private fun createViewModel(): MainViewModel =
        MainViewModel(
            userSession = userSession,
            appManager = appManager,
            analytics = analytics,
            preferences = preferences,
            documentRepository = documentsRepository,
            feedbackRepository = feedbackRepository,
            reportRepository = reportRepository,
            conferencesDataSource = conferencesDataSource,
            offlineQueueConnectivityMonitor = connectivityMonitor,
            firestoreTelemetry = FirestoreTelemetry(),
        )
}
