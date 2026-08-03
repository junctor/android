package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.FlowResult
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Speaker
import com.advice.ui.states.SpeakerState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SpeakerRepositoryTest {
    private val speakersRepository = mockk<SpeakersRepository>()
    private val contentRepository = mockk<ContentRepository>()
    private lateinit var subject: SpeakerRepository

    private val location = Location(1, "Track A", "A")

    @Before
    fun setUp() {
        subject = SpeakerRepository(speakersRepository, contentRepository)
    }

    @Test
    fun missingSpeaker_returnsError() =
        runTest {
            coEvery { speakersRepository.get(42) } returns null

            assertEquals(SpeakerState.Error, subject.getSpeakerDetails(42))
        }

    @Test
    fun speakerWithMatchingSessions_returnsSuccessEvents() =
        runTest {
            val speaker = speaker(id = 7)
            val other = speaker(id = 8)
            val matching =
                content(
                    id = 1,
                    speakers = listOf(speaker),
                    sessions = listOf(session(10), session(11)),
                )
            val unrelated =
                content(
                    id = 2,
                    speakers = listOf(other),
                    sessions = listOf(session(20)),
                )
            val contentFlow = MutableSharedFlow<FlowResult<ConferenceContent>>(replay = 1)
            contentFlow.tryEmit(FlowResult.Success(ConferenceContent(listOf(matching, unrelated))))

            coEvery { speakersRepository.get(7) } returns speaker
            every { contentRepository.content } returns contentFlow

            val state = subject.getSpeakerDetails(7) as SpeakerState.Success

            assertEquals(speaker, state.speaker)
            assertEquals(2, state.events.size)
            assertTrue(state.events.all { it.content.id == 1L })
            assertEquals(listOf(10L, 11L), state.events.map { it.session.id })
        }

    private fun speaker(id: Long) =
        Speaker(
            id = id,
            name = "Speaker $id",
            pronouns = null,
            description = "",
            affiliations = emptyList(),
            links = emptyList(),
            roles = emptyList(),
        )

    private fun content(
        id: Long,
        speakers: List<Speaker>,
        sessions: List<Session>,
    ) = Content(
        id = id,
        conference = "TEST",
        title = "Talk $id",
        description = "",
        updated = Instant.parse("2024-08-01T00:00:00Z"),
        speakers = speakers,
        types = emptyList(),
        urls = emptyList(),
        media = emptyList(),
        sessions = sessions,
    )

    private fun session(id: Long) =
        Session(
            id = id,
            timeZone = "UTC",
            start = Instant.parse("2024-08-10T18:00:00Z"),
            end = Instant.parse("2024-08-10T19:00:00Z"),
            location = location,
            isBookmarked = false,
        )
}
