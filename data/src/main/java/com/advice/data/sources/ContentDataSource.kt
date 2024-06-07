package com.advice.data.sources

import com.advice.core.local.Event

interface ContentDataSource {

    suspend fun get(conference: String, id: Long): Event?
}
