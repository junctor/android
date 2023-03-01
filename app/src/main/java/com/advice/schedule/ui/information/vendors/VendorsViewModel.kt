package com.advice.schedule.ui.information.vendors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.advice.core.utils.Response
import com.advice.schedule.models.local.Vendor
import org.koin.core.KoinComponent

class VendorsViewModel : ViewModel(), KoinComponent {

    private val _vendors = MutableLiveData<Response<List<Vendor>>>()

    fun getVendors(): LiveData<Response<List<Vendor>>> = _vendors

}