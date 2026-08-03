package com.advice.schedule.data.repositories

import com.advice.core.local.FlowResult
import com.advice.core.local.TagType
import com.advice.data.sources.TagsDataSource
import kotlinx.coroutines.flow.Flow

class TagsRepository(
    tagsDataSource: TagsDataSource,
) {
    /** Upstream tags datasource is already shared on the application scope. */
    val tags: Flow<FlowResult<List<TagType>>> = tagsDataSource.get()
}
