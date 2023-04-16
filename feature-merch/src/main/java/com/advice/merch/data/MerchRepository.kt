package com.advice.merch.data

import android.content.Context
import com.advice.data.datasource.MerchDataSource

class MerchRepository(private val context: Context, private val dataSource: MerchDataSource) {

    val merch = dataSource.get(context)

}