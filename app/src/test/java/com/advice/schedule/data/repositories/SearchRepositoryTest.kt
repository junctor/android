package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.FlowResult
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchRepositoryTest {

    private val userSession = mockk<UserSession>()
    private val eventsDataSource = mockk<ContentRepository>()
    private val speakersDataSource = mockk<SpeakersRepository>()
    private val organizationsDataSource = mockk<OrganizationsRepository>()
    private val faqDataSource = mockk<FAQRepository>()
    private val documentsDataSource = mockk<DocumentsRepository>()

    @Test
    fun `return idle when search is empty`() = runTest {
        val subject = getSubject()

        val state = subject.collectState {
            search("")
        }
        assert(state is SearchState.Idle)
    }

    @Test
    fun `return search results when search is 3 or more characters`() = runTest {
        val subject = getSubject()

        val state = subject.collectState {
            search("123")
        }
        assert(state is SearchState.Results)
        assertEquals("123", (state as SearchState.Results).results.query)
    }

    private fun getSubject(): SearchRepository {
        every { userSession.getConference() } returns flowOf(mockk<Conference>())
        every { eventsDataSource.content } returns flowOf(ConferenceContent(emptyList()))
        every { speakersDataSource.speakers } returns flowOf(emptyList())
        every { organizationsDataSource.organizations } returns flowOf(emptyList())
        every { faqDataSource.faqs } returns flowOf(FlowResult.Loading)
        every { documentsDataSource.documents } returns flowOf(emptyList())

        val subject = SearchRepository(
            userSession,
            eventsDataSource, speakersDataSource, organizationsDataSource, faqDataSource,
            documentsDataSource
        )
        return subject
    }

    private suspend fun SearchRepository.collectState(block: SearchRepository.() -> Unit): SearchState {
        block()
        return state.first()
    }
}
