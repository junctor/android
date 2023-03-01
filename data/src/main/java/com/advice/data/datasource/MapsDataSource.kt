package com.advice.data.datasource

import com.advice.core.firebase.FirebaseConferenceMap
import kotlinx.coroutines.flow.Flow

interface MapsDataSource {

    fun get(): Flow<List<FirebaseConferenceMap>>
}