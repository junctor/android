package com.advice.schedule.ui.information.vendors

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.OrganizationsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class OrganizationsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<OrganizationsRepository>()

    val vendors = repository.vendors
    val villages = repository.villages

}