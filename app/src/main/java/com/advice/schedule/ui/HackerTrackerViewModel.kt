package com.advice.schedule.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.core.firebase.FirebaseConferenceMap
import com.advice.schedule.Resource
import com.advice.schedule.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.local.*
import com.advice.core.local.Conference
import org.koin.core.KoinComponent
import org.koin.core.inject

class HackerTrackerViewModel : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val conferences = database.conferences

    val conference: LiveData<Resource<Conference>>
    val bookmarks: LiveData<Resource<List<Event>>>
    val tags: LiveData<Response<List<FirebaseTagType>>>

    val maps: LiveData<Response<List<FirebaseConferenceMap>>>

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
}