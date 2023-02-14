package com.advice.schedule.ui.schedule

import androidx.lifecycle.*
import com.advice.core.utils.Response
import com.advice.core.utils.TimeUtil
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.repository.ScheduleRepository
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTag.Companion.bookmark
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.ui.screens.ScheduleScreenState
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ScheduleRepository>()

    private val state = MutableLiveData<ScheduleScreenState>(ScheduleScreenState.Init)
//    private val elements = MutableLiveData<List<Event>>()

    private val database: DatabaseManager by inject()

    private val source: LiveData<List<Event>>

    private val events = MediatorLiveData<Response<List<Event>>>()
    private val types = MediatorLiveData<Response<List<FirebaseTagType>>>()
    private val searchQuery = MutableLiveData<String?>()

    init {
        viewModelScope.launch {
            repository.list.collect { elements ->
                val days = elements.groupBy { TimeUtil.getDateStamp(it.start.toDate()) }
                state.value = ScheduleScreenState.Success(days)
            }
        }

        source = Transformations.switchMap(database.conference) {
            val result = MutableLiveData<List<Event>>()

            var isFirst = true

            if (it == null) {
                isFirst = true
                events.value = Response.Init
                types.value = Response.Init
            } else {
                types.addSource(database.getTags(it)) {
                    types.value = Response.Success(it)

                    if (isFirst) {
                        isFirst = false
                        events.addSource(database.getSchedule()) {
                            result.value = it
                        }
                    }
                }
            }

            return@switchMap result
        }

        events.addSource(source) { list ->
            viewModelScope.launch {
                events.value = getList(list, searchQuery.value)
            }
        }

        events.addSource(searchQuery) { query ->
            viewModelScope.launch {
                events.value = getList(source.value ?: emptyList(), query)
            }
        }
    }

    fun getState(): LiveData<ScheduleScreenState> = state

    private fun getList(list: List<Event>, query: String?): Response.Success<List<Event>> {
        val data = list.filter {
            query == null || (it.title.contains(query, ignoreCase = true)
                    || it.description.contains(query, ignoreCase = true)
                    || (query.toLongOrNull() != null && query.toLong() == it.id))
        }
        return Response.Success(data)
    }

    fun getSchedule(location: Location): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { it.location.name == location.name } ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(type: FirebaseTag): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { (type.isBookmark && it.isBookmarked) || (it.types.any { it.id == type.id }) }
                ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(speaker: Speaker): LiveData<Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data
                ?.filter { it.speakers.any { it.id == speaker.id } } ?: emptyList()
            result.value = Response.Success(events)
        }

        return result
    }

    fun getSchedule(): LiveData<com.advice.core.utils.Response<List<Event>>> {
        val result = MediatorLiveData<Response<List<Event>>>()

        result.addSource(events) {
            val events = (it as? Response.Success)?.data ?: emptyList()
            val types = types.value?.dObj as? List<FirebaseTagType> ?: emptyList()
            val data = getSchedule(events, types)
            result.value = Response.Success(data)
        }

        result.addSource(types) {
            val events = (events.value as? Response.Success)?.data ?: return@addSource
            val types = (it as? Response.Success)?.data ?: emptyList()
            val data = getSchedule(events, types)
            result.value = Response.Success(data)
        }

        return result
    }

    private fun getSchedule(events: List<Event>, types: List<FirebaseTagType>): List<Event> {
        if (types.isEmpty())
            return events

        val requireBookmark = bookmark.isSelected
        val filter = types.flatMap { it.tags }.filter { !it.isBookmark && it.isSelected }
        if (!requireBookmark && filter.isEmpty())
            return events

        if (requireBookmark && filter.isEmpty())
            return events.filter { it.isBookmarked }

        return events.filter { event -> isShown(event, requireBookmark, filter) }
    }

    private fun isShown(event: Event, requireBookmark: Boolean, filter: List<FirebaseTag>): Boolean {
        // in the event the types are null, default to shown
        if (event.types.isEmpty())
            return true

        val bookmark = if (requireBookmark) {
            event.isBookmarked
        } else {
            true
        }

        return bookmark && event.types.any { t -> filter.find { it.id == t.id }?.isSelected == true }
    }

    fun setSearchQuery(query: String?) {
        searchQuery.value = query
    }

    suspend fun getEventById(target: Long): Event? {
        return try {
            val value = database.conference.value
            if (value == null) {
                Timber.e("Could not get Conference from database")
                return null
            }
            database.getEventById(value.code, target)
        } catch (ex: Exception) {
            Timber.e(ex, "Could not find event by id: $target")
            null
        }
    }

    fun bookmark(event: Event) {
        repository.bookmark(event)
    }
}