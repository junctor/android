package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.core.local.Organization
import com.advice.core.local.Speaker
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.FAQDataSource
import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.SpeakersDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

data class SearchResults(
    val query: String,
    val events: List<Event>,
    val speakers: List<Speaker>,
    val organizations: List<Organization>,
)

class SearchRepository(
    eventsDataSource: EventsDataSource,
    speakersDataSource: SpeakersDataSource,
    organizationsDataSource: OrganizationsDataSource,
    faqDataSource: FAQDataSource,
    documentsDataSource: DocumentsDataSource,
) {
    private var query = MutableStateFlow("")

    val result: Flow<SearchResults> = combine(
        query,
        eventsDataSource.get(),
        speakersDataSource.get(),
        organizationsDataSource.get(),
        faqDataSource.get(),
        documentsDataSource.get(),
    ) { values ->
        val query = values[0] as String
        if (query.length < 3) {
            return@combine SearchResults(
                query = query,
                events = emptyList(),
                speakers = emptyList(),
                organizations = emptyList(),
            )
        }

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
        )
    }

    fun search(query: String) {
        this.query.value = query
    }
}