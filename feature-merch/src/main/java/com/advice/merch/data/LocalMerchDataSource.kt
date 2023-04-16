package com.advice.merch.data

import android.content.Context
import com.advice.core.local.Merch
import com.advice.core.local.MerchDataModel
import com.advice.core.local.toMerch
import com.advice.data.datasource.MerchDataSource
import com.advice.merch.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalMerchDataSource : MerchDataSource {

    override fun get(context: Context): Flow<List<Merch>> = flow {
        val json =
            context.resources.openRawResource(R.raw.merch).bufferedReader().use { it.readText() }
        val data: List<MerchDataModel> =
            Gson().fromJson(json, object : TypeToken<List<MerchDataModel>>() {}.type)
        val merch = data.map { it.toMerch() }
        emit(merch)
    }

}