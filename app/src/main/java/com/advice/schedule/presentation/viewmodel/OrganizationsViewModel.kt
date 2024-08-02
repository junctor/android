package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.core.local.Organization
import com.advice.schedule.data.repositories.OrganizationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OrganizationsViewModel : ViewModel(), KoinComponent {
    private val repository by inject<OrganizationsRepository>()

    fun getState(id: Long): Flow<List<Organization>> {
        return repository.organizations.map {
            it.filter { organization -> organization.tags.contains(id) }
        }
    }
}
