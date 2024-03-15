package com.advice.schedule.data.repositories

import com.advice.data.sources.TagsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class TagsRepository(
    tagsDataSource: TagsDataSource,
) {
    val tags = tagsDataSource.get().shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        replay = 1,
    )
}
