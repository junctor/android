package com.advice.data.sources

import com.advice.core.local.TagType
import kotlinx.coroutines.flow.Flow

interface TagsDataSource {
    fun get(): Flow<List<TagType>>
    suspend fun fetch(conference: String): List<TagType>
}
