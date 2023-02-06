package com.advice.schedule.ui.information.vendors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.core.utils.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Vendor
import org.koin.core.KoinComponent
import org.koin.core.inject

class VendorsViewModel : ViewModel(), KoinComponent {

    private val database by inject<DatabaseManager>()

    private val vendors: LiveData<Response<List<Vendor>>>

    init {
        vendors = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<Response<List<Vendor>>>()

            if (it == null) {
                result.value = Response.Init
            } else {
                result.value = Response.Loading
                result.addSource(database.getVendors(it)) {
                    result.value = Response.Success(it)
                }
            }

            return@switchMap result
        }
    }

    fun getVendors(): LiveData<Response<List<Vendor>>> = vendors

}