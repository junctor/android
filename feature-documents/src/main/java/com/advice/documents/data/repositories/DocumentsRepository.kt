package com.advice.documents.data.repositories

import com.advice.core.local.Document
import com.advice.data.sources.DocumentsDataSource

class DocumentsRepository(
    private val documentsDataSource: DocumentsDataSource,
) {
    val documents = documentsDataSource.get()
    suspend fun get(id: Long): Document? = documentsDataSource.get(id)
}
