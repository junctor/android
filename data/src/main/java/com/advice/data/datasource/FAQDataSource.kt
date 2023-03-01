package com.advice.data.datasource

import com.advice.core.local.FAQ
import kotlinx.coroutines.flow.Flow

interface FAQDataSource {

    fun get(): Flow<List<FAQ>>
}