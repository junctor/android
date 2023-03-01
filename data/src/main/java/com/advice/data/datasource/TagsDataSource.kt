package com.advice.data.datasource

import com.advice.core.local.TagType
import kotlinx.coroutines.flow.Flow


interface TagsDataSource {
    fun get(): Flow<List<TagType>>
}

