package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.FlowResult
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.organizations.data.repositories.OrganizationsRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
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
    private val tagsDataSource = mockk<TagsRepository>()

    private val talkTag = Tag(1, "Talk", "Presentation talks", "#FF0000", 0)
    private val workshopTag = Tag(2, "Workshop", "Hands-on workshops", "#00FF00", 1)
    private val merchSizeTag = Tag(3, "XL", "Extra large", "#0000FF", 0)
    private val villageTag = Tag(4, "Village", "Hardware village", "#FFFF00", 0)

    private val tagTypes =
        listOf(
            TagType(
                id = 1,
                label = "Event Type",
                category = "content",
                isBrowsable = true,
                sortOrder = 0,
                tags = listOf(talkTag, workshopTag),
            ),
            TagType(
                id = 2,
                label = "Sizes",
                category = "merch-variant",
                isBrowsable = true,
                sortOrder = 1,
                tags = listOf(merchSizeTag),
            ),
            TagType(
                id = 3,
                label = "Organization Type",
                category = "content",
                isBrowsable = false,
                sortOrder = 2,
                tags = listOf(villageTag),
            ),
        )

    @Test
    fun `return idle when search is empty`() =
        runTest {
            val subject = getSubject()

            val state =
                subject.collectState {
                    search("")
                }
            assert(state is SearchState.Idle)
        }

    @Test
    fun `return idle when search is one character`() =
        runTest {
            val subject = getSubject()

            val state =
                subject.collectState {
                    search("a")
                }
            assert(state is SearchState.Idle)
        }

    @Test
    fun `return search results when search is 3 or more characters`() =
        runTest {
            val subject = getSubject()

            val state =
                subject.collectState {
                    search("123")
                }
            assert(state is SearchState.Results)
            assertEquals("123", (state as SearchState.Results).results.query)
        }

    @Test
    fun `return matching browsable content tags by label`() =
        runTest {
            val subject = getSubject()

            val state =
                subject.collectState {
                    search("talk")
                }

            assert(state is SearchState.Results)
            val tags = (state as SearchState.Results).results.tags
            assertEquals(listOf(talkTag), tags)
        }

    @Test
    fun `return matching browsable content tags by description`() =
        runTest {
            val subject = getSubject()

            val state =
                subject.collectState {
                    search("hands-on")
                }

            assert(state is SearchState.Results)
            val tags = (state as SearchState.Results).results.tags
            assertEquals(listOf(workshopTag), tags)
        }

    @Test
    fun `exclude non-content and non-browsable tags`() =
        runTest {
            val subject = getSubject()

            val merchState =
                subject.collectState {
                    search("XL")
                }
            assert(merchState is SearchState.Results)
            assertTrue((merchState as SearchState.Results).results.tags.isEmpty())

            val villageState =
                subject.collectState {
                    search("Village")
                }
            assert(villageState is SearchState.Results)
            assertTrue((villageState as SearchState.Results).results.tags.isEmpty())
        }

    private fun getSubject(): SearchRepository {
        every { userSession.getConference() } returns flowOf(mockk<Conference>())
        every { eventsDataSource.content } returns
            MutableSharedFlow<FlowResult<ConferenceContent>>(replay = 1).apply {
                tryEmit(FlowResult.Success(ConferenceContent(emptyList())))
            }
        every { speakersDataSource.speakers } returns flowOf(emptyList())
        every { organizationsDataSource.organizations } returns flowOf(emptyList())
        every { faqDataSource.faqs } returns flowOf(FlowResult.Loading)
        every { documentsDataSource.documents } returns flowOf(emptyList())
        every { tagsDataSource.tags } returns
            MutableSharedFlow<FlowResult<List<TagType>>>(replay = 1).apply {
                tryEmit(FlowResult.Success(tagTypes))
            }

        return SearchRepository(
            userSession,
            eventsDataSource,
            speakersDataSource,
            organizationsDataSource,
            faqDataSource,
            documentsDataSource,
            tagsDataSource,
        )
    }

    private suspend fun SearchRepository.collectState(block: SearchRepository.() -> Unit): SearchState {
        block()
        return state.first()
    }
}
