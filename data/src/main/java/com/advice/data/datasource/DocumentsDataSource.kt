package com.advice.data.datasource

import com.advice.core.local.Document
import kotlinx.coroutines.flow.Flow

interface DocumentsDataSource {

    fun get(): Flow<List<Document>>
}