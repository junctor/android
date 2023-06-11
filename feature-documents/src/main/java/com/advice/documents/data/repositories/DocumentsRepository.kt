package com.advice.documents.data.repositories

import com.advice.data.sources.DocumentsDataSource

class DocumentsRepository(
    private val documentsDataSource: DocumentsDataSource,
) {
    val documents = documentsDataSource.get()
}