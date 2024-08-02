package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.organizations.ui.screens.OrganizationScreenState
import com.advice.schedule.data.repositories.OrganizationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class OrganizationViewModel : ViewModel(), KoinComponent {
    private val repository by inject<OrganizationsRepository>()

    private val _state = MutableStateFlow<OrganizationScreenState>(OrganizationScreenState.Loading)
    val state: StateFlow<OrganizationScreenState> = _state

    fun getOrganization(id: Long?) {
        if (id == null) {
            _state.value = OrganizationScreenState.Error
            return
        }

        viewModelScope.launch {
            _state.value = OrganizationScreenState.Loading
            val organization = repository.get(id)
            if (organization != null) {
                _state.value = OrganizationScreenState.Success(organization)
            } else {
                _state.value = OrganizationScreenState.Error
            }
        }
    }
}
