package com.advice.data.datasource

import com.advice.core.local.Speaker
import kotlinx.coroutines.flow.Flow

interface SpeakersDataSource {

    fun get(): Flow<List<Speaker>>
}