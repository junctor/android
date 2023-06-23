package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.OrganizationsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OrganizationsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<OrganizationsRepository>()

    val vendors = repository.vendors
    val villages = repository.villages

}