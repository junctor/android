package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import com.advice.core.local.Organization
import com.advice.core.local.Speaker
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

sealed class SearchState {
    object Idle : SearchState()

    data class Results(
        val results: SearchResults,
    ) : SearchState()
}

data class SearchResults(
    val query: String,
    val contents: List<Content>,
    val speakers: List<Speaker>,
    val organizations: List<Organization>,
    val faq: List<FAQ>,
    val documents: List<Document>,
) {
    val events = contents.flatMap { content ->
        content.sessions.map { session ->
            Event(content, session)
        }
    }
}

class SearchRepository(
    userSession: UserSession,
    eventsDataSource: ContentRepository,
    speakersDataSource: SpeakersRepository,
    organizationsDataSource: OrganizationsRepository,
    faqDataSource: FAQRepository,
    documentsDataSource: DocumentsRepository,
) {
    val conference = userSession.getConference()

    private var query = MutableStateFlow("")

    val state: Flow<SearchState> = combine(
        query,
        eventsDataSource.content,
        speakersDataSource.speakers,
        organizationsDataSource.organizations,
        combine(faqDataSource.faqs, documentsDataSource.documents, ::Pair),
    ) { query, conferenceContent, speakers, organizations, faqsAndDocuments ->
        if (query.length < MIN_QUERY_LENGTH) {
            return@combine SearchState.Idle
        }

        val (faqs, documents) = faqsAndDocuments
        val faqList = when (faqs) {
            is FlowResult.Success -> faqs.value
            else -> emptyList()
        }

        SearchState.Results(
            SearchResults(
                query = query,
                contents = conferenceContent.content.filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true)
                },
                speakers = speakers.filter { speaker ->
                    speaker.name.contains(query, ignoreCase = true)
                },
                organizations = organizations.filter { organization ->
                    organization.name.contains(query, ignoreCase = true)
                },
                faq = faqList.filter { faq ->
                    faq.question.contains(query, ignoreCase = true) ||
                        faq.answer.contains(query, ignoreCase = true)
                },
                documents = documents.filter { document ->
                    document.title.contains(query, ignoreCase = true) ||
                        document.description.contains(query, ignoreCase = true)
                },
            ),
        )
    }

    fun search(query: String) {
        this.query.value = query
    }
}

private const val MIN_QUERY_LENGTH = 2
