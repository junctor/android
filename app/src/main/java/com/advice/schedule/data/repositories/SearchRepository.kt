package com.advice.schedule.data.repositories

import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.core.local.FAQ
import com.advice.core.local.Organization
import com.advice.core.local.Speaker
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

sealed class SearchState {
    object Idle : SearchState()
    data class Results(val results: SearchResults) : SearchState()
}

data class SearchResults(
    val query: String,
    val events: List<Event>,
    val speakers: List<Speaker>,
    val organizations: List<Organization>,
    val faq: List<FAQ>,
    val documents: List<Document>,
)

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
        val query = values[QUERY_INDEX] as String
        if (query.length < MIN_QUERY_LENGTH) {
            return@combine SearchState.Idle
        }

        SearchState.Results(
            SearchResults(
                query = query,
                events = (values[EVENTS_INDEX] as List<Event>).filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                            event.description.contains(query, ignoreCase = true)
                },
                speakers = (values[SPEAKERS_INDEX] as List<Speaker>).filter { speaker ->
                    speaker.name.contains(query, ignoreCase = true)
                },
                organizations = (values[ORGANIZATIONS_INDEX] as List<Organization>).filter { organization ->
                    organization.name.contains(query, ignoreCase = true)
                },
                faq = (values[FAQ_INDEX] as List<FAQ>).filter { faq ->
                    faq.question.contains(query, ignoreCase = true) ||
                            faq.answer.contains(query, ignoreCase = true)
                },
                documents = (values[DOCUMENTS_INDEX] as List<Document>).filter { document ->
                    document.title.contains(query, ignoreCase = true) ||
                            document.description.contains(query, ignoreCase = true)
                },
            ),
        )
    }

    fun search(query: String) {
        this.query.value = query
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 2
        private const val QUERY_INDEX = 0
        private const val EVENTS_INDEX = 1
        private const val SPEAKERS_INDEX = 2
        private const val ORGANIZATIONS_INDEX = 3
        private const val FAQ_INDEX = 4
        private const val DOCUMENTS_INDEX = 5
    }
}
