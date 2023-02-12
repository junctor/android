package com.advice.schedule.ui.information.speakers

import androidx.lifecycle.*
import com.advice.core.utils.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.repository.SpeakersRepository
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.utilities.Analytics
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SpeakersRepository>()

    private val analytics: Analytics by inject()

    val speakers = MutableLiveData<List<Speaker>>()


    init {
        viewModelScope.launch {
            repository.list.collect {
                speakers.value = it
            }
        }
    }

    fun getSpeaker(speaker: Speaker): LiveData<Response<Speaker>> {
        analytics.log("Viewing speaker ${speaker.name}")
        analytics.onSpeakerView(speaker)

        val result = MediatorLiveData<Response<Speaker>>()
//        result.value = Response.Success(speaker)
//        result.addSource(source) {
//            viewModelScope.launch {
//                val element = it.find { it.id == speaker.id }
//                if (element != null) {
//                    result.postValue(Response.Success(element))
//                } else {
//                    result.postValue(Response.Error(IllegalStateException("Speaker not found")))
//                }
//            }
//        }

        return result
    }

    fun getSpeakerEvents(speaker: Speaker): LiveData<List<Event>> {
        val result = MediatorLiveData<List<Event>>()

        return result
    }

    fun onOpenTwitter(speaker: Speaker) {
        analytics.onSpeakerEvent(speaker)
    }

//    fun getSpeakers(): LiveData<Response<List<Speaker>>> = speakers

}