package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Tag
import com.advice.core.ui.FiltersScreenState
import com.advice.schedule.data.repositories.FiltersRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class FiltersViewModel : ViewModel(), KoinComponent {


    private val repository by inject<FiltersRepository>()

    val state = flow {
        emit(FiltersScreenState.Init)
        repository.tags.collect {
            emit(FiltersScreenState.Success(it))
        }
    }

    fun toggle(tag: Tag) {
        Timber.e("tag: $tag")
        viewModelScope.launch {
            repository.toggle(tag)
        }
    }

    fun clearBookmarks() {
        viewModelScope.launch {
            repository.clear()
        }
    }
}

