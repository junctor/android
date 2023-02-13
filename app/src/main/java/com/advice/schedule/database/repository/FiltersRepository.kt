package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag
import timber.log.Timber

class FiltersRepository(private val tagsDataSource: TagsDataSource) {


    val tags = tagsDataSource.get()

    fun toggle(tag: FirebaseTag) {
        Timber.e("tag: $tag")
        tagsDataSource.updateTypeIsSelected(tag)
    }

}