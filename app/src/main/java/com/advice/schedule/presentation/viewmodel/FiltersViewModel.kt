package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Tag
import com.advice.core.ui.FiltersScreenState
import com.advice.schedule.data.repositories.FiltersRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class FiltersViewModel(
    private val repository: FiltersRepository,
) : ViewModel() {
    val state =
        flow {
            emit(FiltersScreenState.Loading)
            repository.state.collect {
                emit(it)
            }
        }

    fun toggle(tag: Tag) {
        Timber.i("User toggled the tag: $tag")
        viewModelScope.launch {
            repository.toggle(tag)
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            repository.clearFilters()
        }
    }
}
