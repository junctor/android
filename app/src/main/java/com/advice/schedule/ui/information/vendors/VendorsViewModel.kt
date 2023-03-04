package com.advice.schedule.ui.information.vendors

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.VendorsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class VendorsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<VendorsRepository>()

    val vendors = repository.vendors

}