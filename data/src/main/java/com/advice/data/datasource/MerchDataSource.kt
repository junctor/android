package com.advice.data.datasource

import android.content.Context
import com.advice.core.local.Merch
import kotlinx.coroutines.flow.Flow

interface MerchDataSource {

    fun get(context: Context): Flow<List<Merch>>
}