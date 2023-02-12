package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag

class FiltersRepository(private val tagsDataSource: TagsDataSource) {


    val tags = tagsDataSource.get()

    suspend fun toggle(tag: FirebaseTag) {
        tagsDataSource.updateTypeIsSelected(tag)
    }

}