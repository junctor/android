package com.advice.schedule.data.repositories

import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.core.local.Organization
import com.advice.core.local.Speaker
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
    val documents: List<Document>,
)

class SearchRepository(
    eventsDataSource: EventsRepository,
    speakersDataSource: SpeakersRepository,
    organizationsDataSource: OrganizationsRepository,
    faqDataSource: FAQRepository,
    documentsDataSource: DocumentsRepository,
) {
    private var query = MutableStateFlow("")

    val state: Flow<SearchState> = combine(
        query,
        eventsDataSource.events,
        speakersDataSource.speakers,
        organizationsDataSource.organizations,
        faqDataSource.faqs,
        documentsDataSource.documents,
    ) { values ->
        val query = values[0] as String
        if (query.length < 3) {
            return@combine SearchState.Idle
        }

        SearchState.Results(
            SearchResults(
                query = query,
                events = (values[1] as List<Event>).filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                            event.description.contains(query, ignoreCase = true)
                },
                speakers = (values[2] as List<Speaker>).filter { speaker ->
                    speaker.name.contains(query, ignoreCase = true)
                },
                organizations = (values[3] as List<Organization>).filter { organization ->
                    organization.name.contains(query, ignoreCase = true)
                },
                documents = (values[5] as List<Document>).filter { document ->
                    document.title.contains(query, ignoreCase = true) ||
                            document.description.contains(query, ignoreCase = true)
                },
            )
        )
    }

    fun search(query: String) {
        this.query.value = query
    }
}