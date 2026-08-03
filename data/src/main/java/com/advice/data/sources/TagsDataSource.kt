package com.advice.data.sources

import com.advice.core.local.FlowResult
import com.advice.core.local.TagType
import kotlinx.coroutines.flow.Flow

interface TagsDataSource {
    fun get(): Flow<FlowResult<List<TagType>>>
}
