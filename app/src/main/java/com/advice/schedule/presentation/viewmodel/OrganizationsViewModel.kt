package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.core.local.Organization
import com.advice.schedule.data.repositories.OrganizationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class OrganizationsViewModel : ViewModel(), KoinComponent {
    private val repository by inject<OrganizationsRepository>()

    fun getState(id: String): Flow<List<Organization>> {
        return repository.organizations.map {
            it.filter { organization -> organization.tags.contains(id.toLong()) }
        }
    }

    fun getOrganization(id: Long?): Flow<Organization?> {
        return flow {
            if (id == null) {
                Timber.e("Conference or id is null")
                emit(null)
                return@flow
            }

            val value = repository.find(id)
            emit(value)
        }
    }
}
