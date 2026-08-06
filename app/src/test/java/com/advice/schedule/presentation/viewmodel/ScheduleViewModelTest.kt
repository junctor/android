package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.Conference
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.ScheduleDayFormat
import com.advice.core.local.Session
import com.advice.data.session.UserSession
import com.advice.data.storage.UserPreferencesStore
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.ScheduleResult
import com.advice.ui.states.ScheduleFilter
import com.advice.ui.states.ScheduleScreenState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val repository = mockk<ScheduleRepository>(relaxed = true)
    private val storage = mockk<UserPreferencesStore>()
    private val userSession = mockk<UserSession>(relaxed = true)

    private val location = Location(1, "Track A", "A")

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { storage.scheduleDayFormatFlow } returns MutableStateFlow(ScheduleDayFormat.MonthDay)
        every { storage.forceTimeZone } returns true
        every { storage.showFilters } returns true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success groups events into days by date stamp`() =
        runTest {
            val events =
                listOf(
                    event(1, start = Instant.parse("2024-08-10T18:00:00Z")),
                    event(2, start = Instant.parse("2024-08-10T20:00:00Z")),
                    event(3, start = Instant.parse("2024-08-11T18:00:00Z")),
                )
            every { repository.getSchedule(ScheduleFilter.Default) } returns
                flowOf(ScheduleResult.Success(events))

            val state = createViewModel().getState().first()

            assertTrue(state is ScheduleScreenState.Success)
            state as ScheduleScreenState.Success
            assertEquals(2, state.days.size)
            assertEquals(events, state.days.values.flatten())
            assertTrue(state.showFab)
        }

    @Test
    fun `loading result maps to loading state`() =
        runTest {
            every { repository.getSchedule(ScheduleFilter.Default) } returns
                flowOf(ScheduleResult.Loading)

            val state = createViewModel().getState().first()

            assertTrue(state is ScheduleScreenState.Loading)
        }

    @Test
    fun `empty result maps to empty state with message`() =
        runTest {
            every { repository.getSchedule(ScheduleFilter.Default) } returns
                flowOf(ScheduleResult.Empty("No events found"))

            val state = createViewModel().getState().first()

            assertTrue(state is ScheduleScreenState.Empty)
            assertEquals("No events found", (state as ScheduleScreenState.Empty).message)
        }

    @Test
    fun `error result maps to error state with message`() =
        runTest {
            every { repository.getSchedule(ScheduleFilter.Default) } returns
                flowOf(ScheduleResult.Error("Could not load schedule"))

            val state = createViewModel().getState().first()

            assertTrue(state is ScheduleScreenState.Error)
            assertEquals("Could not load schedule", (state as ScheduleScreenState.Error).error)
        }

    @Test
    fun `bookmark delegates to repository`() =
        runTest {
            val subject = event(1, start = Instant.parse("2024-08-10T18:00:00Z"))

            createViewModel().bookmark(subject, isBookmarked = true)
            advanceUntilIdle()

            coVerify { repository.bookmark(subject.content, subject.session, true) }
        }

    @Test
    fun `retry re-selects the current conference`() {
        val conference = Conference.Zero.copy(id = 1, name = "Alpha")
        every { userSession.currentConference } returns conference

        createViewModel().retry()

        verify { userSession.setConference(conference) }
    }

    @Test
    fun `retry without a conference does nothing`() {
        every { userSession.currentConference } returns null

        createViewModel().retry()

        verify(exactly = 0) { userSession.setConference(any()) }
    }

    private fun createViewModel(): ScheduleViewModel = ScheduleViewModel(storage, repository, userSession)

    private fun event(
        id: Long,
        start: Instant,
    ): Event =
        Event(
            content =
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
                ),
            session =
                Session(
                    id = id,
                    timeZone = "UTC",
                    start = start,
                    end = start.plusSeconds(3600),
                    location = location,
                ),
        )
}
