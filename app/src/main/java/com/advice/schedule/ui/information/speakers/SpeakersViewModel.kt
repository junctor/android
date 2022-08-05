package com.advice.schedule.ui.information.speakers

import androidx.lifecycle.*
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.utilities.Analytics
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()

    private val source: LiveData<List<Speaker>>

    private val speakers = MediatorLiveData<Response<List<Speaker>>>()
    private val searchQuery = MutableLiveData<String?>()

    init {
        source = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<List<Speaker>>()

            if (it != null) {
                result.addSource(database.getSpeakers(it)) {
                    result.value = it
                }
            }

            return@switchMap result
        }

        speakers.addSource(source) {
            val query = searchQuery.value
            speakers.value = getList(it, query)
        }

        speakers.addSource(searchQuery) { query ->
            val list = source.value ?: emptyList()
            speakers.value = getList(list, query)
        }
    }

    private fun getList(
        it: List<Speaker>,
        query: String?
    ): Response.Success<List<Speaker>> {
        val data = it.filter { query == null || it.name.contains(query, ignoreCase = true) || it.title.contains(query, ignoreCase = true) }
        return Response.Success(data)
    }

    fun setSearchQuery(query: String?) {
        searchQuery.value = query
    }

    fun getSpeaker(speaker: Speaker): LiveData<Response<Speaker>> {
        analytics.log("Viewing speaker ${speaker.name}")
        analytics.onSpeakerEvent(Analytics.SPEAKER_VIEW, speaker)

        val result = MediatorLiveData<Response<Speaker>>()
        result.value = Response.Success(speaker)
        result.addSource(source) {
            viewModelScope.launch {
                val element = it.find { it.id == speaker.id }
                if (element != null) {
                    result.postValue(Response.Success(element))
                } else {
                    result.postValue(Response.Error(IllegalStateException("Speaker not found")))
                }
            }
        }

        return result
    }

    fun getSpeakerEvents(speaker: Speaker): LiveData<List<Event>> {
        val result = MediatorLiveData<List<Event>>()

        result.addSource(database.getEventsForSpeaker(speaker)) { list ->
            result.postValue(list)
        }

        return result
    }

    fun onOpenTwitter(speaker: Speaker) {
        analytics.onSpeakerEvent(Analytics.SPEAKER_TWITTER, speaker)
    }

    fun getSpeakers(): LiveData<Response<List<Speaker>>> = speakers

}