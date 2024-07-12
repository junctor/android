package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
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
        faqDataSource.faqs,
        documentsDataSource.documents,
    ) { values ->
        val data = values.toSearchableData()
        val query = values[QUERY_INDEX] as String
        if (query.length < MIN_QUERY_LENGTH) {
            return@combine SearchState.Idle
        }

        SearchState.Results(
            SearchResults(
                query = data.query,
                contents = data.events.filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true)
                },
                speakers = data.speakers.filter { speaker ->
                    speaker.name.contains(query, ignoreCase = true)
                },
                organizations = data.organizations.filter { organization ->
                    organization.name.contains(query, ignoreCase = true)
                },
                faq = data.faq.filter { faq ->
                    faq.question.contains(query, ignoreCase = true) ||
                        faq.answer.contains(query, ignoreCase = true)
                },
                documents = data.documents.filter { document ->
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

data class SearchableData(
    val query: String,
    val events: List<Content>,
    val speakers: List<Speaker>,
    val organizations: List<Organization>,
    val faq: List<FAQ>,
    val documents: List<Document>,
)

private fun <T> Array<T>.toSearchableData(): SearchableData {
    return SearchableData(
        query = this[QUERY_INDEX] as String,
        events = (this[EVENTS_INDEX] as? ConferenceContent)?.content ?: emptyList(),
        speakers = this[SPEAKERS_INDEX] as? List<Speaker> ?: emptyList(),
        organizations = this[ORGANIZATIONS_INDEX] as? List<Organization> ?: emptyList(),
        faq = (this[FAQ_INDEX] as FlowResult<List<FAQ>> as? FlowResult.Success)?.value
            ?: emptyList(),
        documents = this[DOCUMENTS_INDEX] as? List<Document> ?: emptyList(),
    )
}

private const val MIN_QUERY_LENGTH = 2
private const val QUERY_INDEX = 0
private const val EVENTS_INDEX = 1
private const val SPEAKERS_INDEX = 2
private const val ORGANIZATIONS_INDEX = 3
private const val FAQ_INDEX = 4
private const val DOCUMENTS_INDEX = 5
