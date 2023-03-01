package com.advice.data.datasource

import com.advice.schedule.models.firebase.FirebaseTagType
import kotlinx.coroutines.flow.Flow


interface TagsDataSource {
    fun get(): Flow<List<FirebaseTagType>>
}

