package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.ui.states.EventScreenState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class EventViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val scheduleRepository = mockk<ScheduleRepository>(relaxed = true)
    private val contentRepository = mockk<ContentRepository>()

    private val location = Location(1, "Track A", "A")

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `null id emits invalid event error`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.getEvent(conference = "TEST", id = null, session = null)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is EventScreenState.Error)
            assertEquals("Invalid event id", (state as EventScreenState.Error).message)
        }

    @Test
    fun `missing content emits not found error`() =
        runTest {
            coEvery { contentRepository.getContent("TEST", 1L) } returns null
            val viewModel = createViewModel()

            viewModel.getEvent(conference = "TEST", id = 1L, session = null)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is EventScreenState.Error)
            assertEquals("Content not found", (state as EventScreenState.Error).message)
        }

    @Test
    fun `loads event with matching session and related content`() =
        runTest {
            val subject =
                content(
                    id = 1,
                    sessions = listOf(session(10), session(11)),
                    relatedContentIds = listOf(2L),
                )
            val related = content(id = 2)
            coEvery { contentRepository.getContent("TEST", 1L) } returns subject
            coEvery { contentRepository.getContent("TEST", 2L) } returns related
            val viewModel = createViewModel()

            viewModel.getEvent(conference = "TEST", id = 1L, session = 11L)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is EventScreenState.Success)
            state as EventScreenState.Success
            assertEquals(subject, state.content)
            assertEquals(11L, state.session?.id)
            assertEquals(listOf(related), state.relatedContent)
        }

    @Test
    fun `bookmark delegates to repository and refreshes the loaded event`() =
        runTest {
            val subject = content(id = 1, sessions = listOf(session(10)))
            coEvery { contentRepository.getContent("TEST", 1L) } returns subject
            val viewModel = createViewModel()
            viewModel.getEvent(conference = "TEST", id = 1L, session = 10L)
            advanceUntilIdle()

            viewModel.bookmark(subject, subject.sessions.first(), isBookmarked = true)
            advanceUntilIdle()

            coVerify { scheduleRepository.bookmark(subject, subject.sessions.first(), true) }
            // Once for the initial load, once for the post-bookmark refresh.
            coVerify(exactly = 2) { contentRepository.getContent("TEST", 1L) }
        }

    private fun createViewModel(): EventViewModel = EventViewModel(scheduleRepository, contentRepository)

    private fun content(
        id: Long,
        sessions: List<Session> = emptyList(),
        relatedContentIds: List<Long> = emptyList(),
    ): Content =
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
            sessions = sessions,
            relatedContentIds = relatedContentIds,
        )

    private fun session(id: Long): Session {
        val start = Instant.parse("2024-08-10T18:00:00Z")
        return Session(
            id = id,
            timeZone = "UTC",
            start = start,
            end = start.plusSeconds(3600),
            location = location,
        )
    }
}
