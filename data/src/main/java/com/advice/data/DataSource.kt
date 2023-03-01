package com.advice.data

import kotlinx.coroutines.flow.Flow

interface  DataSource<T> {

    fun get(): Flow<List<T>>

    suspend fun clear()
}