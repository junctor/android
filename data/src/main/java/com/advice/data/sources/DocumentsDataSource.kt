package com.advice.data.sources

import com.advice.core.local.Document
import kotlinx.coroutines.flow.Flow

interface DocumentsDataSource {

    fun get(): Flow<List<Document>>
}
