package com.advice.data.sources

import com.advice.core.local.Event

interface EventDataSource {

    suspend fun get(conference: String, id: Long): Event?
}