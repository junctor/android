package com.advice.schedule.presentation.viewmodel

import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.Document
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.play.AppManager
import com.advice.reminder.NotificationHelper
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.ui.states.HomeState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val contentsFlow = MutableSharedFlow<HomeState>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<HomeRepository>(relaxed = true) {
            every { contents } returns contentsFlow
        }
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)
    private val appManager =
        mockk<AppManager> {
            coEvery { isUpdateAvailable() } returns false
        }
    private val notificationHelper = mockk<NotificationHelper>(relaxed = true)
    private val documentsRepository = mockk<DocumentsRepository>()

    // Kickoff in the past keeps the wall-clock countdown loop from starting in tests.
    private val conference =
        Conference.Zero.copy(
            code = "TEST",
            emergencyDocumentId = null,
            kickoffDate = Instant.now().minusSeconds(3600),
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
    fun `loaded state includes update availability`() =
        runTest {
            coEvery { appManager.isUpdateAvailable() } returns true
            val viewModel = createViewModel()

            contentsFlow.emit(loaded())
            advanceUntilIdle()

            val state = viewModel.getHomeState().first()
            assertTrue(state is HomeState.Loaded)
            assertTrue((state as HomeState.Loaded).isUpdateAvailable)
        }

    @Test
    fun `error state passes through`() =
        runTest {
            val viewModel = createViewModel()

            contentsFlow.emit(HomeState.Error(IllegalStateException("boom")))
            advanceUntilIdle()

            assertTrue(viewModel.getHomeState().first() is HomeState.Error)
        }

    @Test
    fun `emergency notification fires once per document id`() =
        runTest {
            val document = Document(5, "Emergency", "Details")
            coEvery { documentsRepository.get(5) } returns document
            val emergency = conference.copy(emergencyDocumentId = 5)
            createViewModel()

            contentsFlow.emit(loaded(emergency))
            advanceUntilIdle()
            verify(exactly = 1) { notificationHelper.notifyEmergency(document, emergency.code) }

            // Re-emitting the same conference does not re-notify.
            contentsFlow.emit(loaded(emergency))
            advanceUntilIdle()
            verify(exactly = 1) { notificationHelper.notifyEmergency(document, emergency.code) }
        }

    @Test
    fun `countdown is zero when kickoff has passed`() =
        runTest {
            val viewModel = createViewModel()

            contentsFlow.emit(loaded())
            advanceUntilIdle()

            assertEquals(0L, viewModel.getCountdown().value)
        }

    @Test
    fun `retry resets state to loading and refreshes the conference`() =
        runTest {
            val viewModel = createViewModel()
            contentsFlow.emit(loaded())
            advanceUntilIdle()

            viewModel.retry()

            assertTrue(viewModel.getHomeState().first() is HomeState.Loading)
            verify { repository.refreshConference() }
        }

    @Test
    fun `set conference delegates and logs analytics`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.setConference(conference)
            advanceUntilIdle()

            verify { repository.setConference(conference) }
            verify { analytics.onConferenceChangeEvent(conference) }
            assertEquals(0L, viewModel.getCountdown().value)
        }

    @Test
    fun `mark latest news as read clears news and delegates`() =
        runTest {
            val article = NewsArticle(1, "Update", "Text", date = null)
            val viewModel = createViewModel()
            contentsFlow.emit(loaded(news = article))
            advanceUntilIdle()

            viewModel.markLatestNewsAsRead(article)
            advanceUntilIdle()

            val state = viewModel.getHomeState().first()
            assertTrue(state is HomeState.Loaded)
            assertNull((state as HomeState.Loaded).news)
            verify { repository.markLatestNewsAsRead(article) }
        }

    private fun createViewModel(): HomeViewModel =
        HomeViewModel(
            repository = repository,
            analytics = analytics,
            appManager = appManager,
            notificationHelper = notificationHelper,
            documentRepository = documentsRepository,
        )

    private fun loaded(
        conference: Conference = this.conference,
        news: NewsArticle? = null,
    ): HomeState.Loaded =
        HomeState.Loaded(
            conferences = listOf(conference),
            conference = conference,
            menu = Menu(1, "Home", emptyList()),
            news = news,
        )
}
