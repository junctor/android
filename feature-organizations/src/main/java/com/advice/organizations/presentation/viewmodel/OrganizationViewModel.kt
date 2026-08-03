package com.advice.organizations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.organizations.data.repositories.OrganizationsRepository
import com.advice.organizations.ui.screens.OrganizationScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrganizationViewModel(
    private val repository: OrganizationsRepository,
) : ViewModel() {
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
