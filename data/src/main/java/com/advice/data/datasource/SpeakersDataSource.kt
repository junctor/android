package com.advice.data.datasource

import com.advice.schedule.models.local.Speaker
import kotlinx.coroutines.flow.Flow

interface SpeakersDataSource {

    fun get(): Flow<List<Speaker>>
}