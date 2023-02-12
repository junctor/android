package com.advice.schedule.database.datasource

import kotlinx.coroutines.flow.Flow

interface  DataSource<T> {

    fun get(): Flow<List<T>>

    fun clear()
}