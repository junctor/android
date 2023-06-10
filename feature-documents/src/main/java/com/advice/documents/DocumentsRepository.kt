package com.advice.documents

import com.advice.data.datasource.DocumentsDataSource

class DocumentsRepository(
    private val documentsDataSource: DocumentsDataSource,
) {
    val documents = documentsDataSource.get()
}