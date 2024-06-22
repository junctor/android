package com.advice.data.sources

import com.advice.core.local.Content
import com.advice.core.local.Event

interface ContentDataSource {

    suspend fun getContent(conference: String, id: Long): Content?
    suspend fun getEvent(conference: String, id: Long): Event?
}
