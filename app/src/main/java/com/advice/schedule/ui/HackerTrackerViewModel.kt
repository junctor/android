package com.advice.schedule.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.schedule.Resource
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseConferenceMap
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.local.*
import com.advice.schedule.utilities.Analytics
import org.koin.core.KoinComponent
import org.koin.core.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val conferences = database.conferences

    val conference: LiveData<Resource<Conference>>
    val events: LiveData<Resource<List<Event>>>
    val bookmarks: LiveData<Resource<List<Event>>>
    val tags: LiveData<Response<List<FirebaseTagType>>>
    val types: LiveData<Resource<List<Type>>>
    val locations: LiveData<Resource<List<Location>>>
    val speakers: LiveData<Response<List<Speaker>>>

    val articles: LiveData<Resource<List<Article>>>

    val maps: LiveData<Response<List<FirebaseConferenceMap>>>

    // Home
    val home: LiveData<Resource<List<Any>>>

    init {
        conference = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<Conference>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.success(it)
            }

            return@switchMap result
        }

        tags = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Response<List<FirebaseTagType>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.value = Response.Loading
                result.addSource(database.getTags(it)) {
                    result.value = Response.Success(it)
                }
            }
            return@switchMap result
        }

        types = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Type>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.loading(null)
                result.addSource(database.getTypes(it)) {
                    result.value = Resource.success(it)
                }
            }
            return@switchMap result
        }

        locations = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Location>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.loading(null)
                result.addSource(database.getLocations(it)) {
                    result.value = Resource.success(it)
                }
            }
            return@switchMap result
        }

        events = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Event>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.loading(null)
                result.addSource(database.getSchedule()) {
                    result.value = Resource.success(it)
                }
            }
            return@switchMap result
        }

        bookmarks = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Event>>>()

            if (it == null) {
                result.value = Resource.init(null)
                return@switchMap result
            } else {
                result.value = Resource.loading(null)
                result.addSource(database.getBookmarks(it)) {
                    result.value = Resource.success(it)
                }
            }

            return@switchMap result
        }

        speakers = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Response<List<Speaker>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.value = Response.Loading
                result.addSource(database.getSpeakers(it)) {
                    result.value = Response.Success(it)
                }
            }

            return@switchMap result
        }

        articles = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Resource<List<Article>>>()

            if (it == null) {
                result.value = Resource.init()
            } else {
                result.value = Resource.loading(null)
                result.addSource(database.getArticles(it)) {
                    result.value = Resource.success(it)
                }
            }

            return@switchMap result
        }


        maps = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Response<List<FirebaseConferenceMap>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.value = Response.Loading
                result.addSource(database.getMaps(it)) {
                    result.value = Response.Success(it)
                }
            }

            return@switchMap result
        }

        home = Transformations.switchMap(database.conference) { id ->
            val result = MediatorLiveData<Resource<List<Any>>>()

            if (id == null) {
                result.value = Resource.init(null)
                return@switchMap result
            }

            result.value = Resource.loading(null)

            result.addSource(articles) {
                val articles = it.data?.take(4) ?: emptyList()
                val bookmarks =
                    bookmarks.value?.data?.filter { !it.hasFinished }?.take(3) ?: emptyList()
                setHome(result, articles, bookmarks)
            }

            return@switchMap result
        }

    }

    private fun setHome(
        result: MediatorLiveData<Resource<List<Any>>>,
        articles: List<Article>,
        bookmarks: List<Event>
    ) {
        if (bookmarks.isEmpty()) {
            result.value = Resource.success(articles)
        } else {
            result.value = Resource.success(articles + "Bookmarks" + bookmarks)
        }
    }

    fun toggleFilter(type: FirebaseTag) {
        type.isSelected = !type.isSelected
        database.updateTypeIsSelected(type)
    }

    fun clearFilters() {
        FirebaseTag.bookmark.isSelected = false
        database.updateTypeIsSelected(FirebaseTag.bookmark)

        val types = tags.value?.dObj as? List<FirebaseTagType> ?: emptyList()
        types.forEach {
            it.tags.forEach {
                if (it.isSelected) {
                    it.isSelected = false
                    database.updateTypeIsSelected(it)
                }
            }
        }
    }

    fun changeConference(conference: Conference) {
        //analytics.
        database.conference.value = null
        database.changeConference(conference.id)
    }
}