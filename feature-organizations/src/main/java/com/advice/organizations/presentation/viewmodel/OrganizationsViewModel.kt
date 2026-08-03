package com.advice.organizations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.core.local.Organization
import com.advice.organizations.data.repositories.OrganizationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrganizationsViewModel(
    private val repository: OrganizationsRepository,
) : ViewModel() {
    fun getState(id: Long): Flow<List<Organization>> =
        repository.organizations.map {
            it.filter { organization -> organization.tags.contains(id) }
        }
}
