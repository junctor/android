package com.advice.schedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.ui.FiltersScreenState
import com.advice.schedule.database.repository.FiltersRepository
import com.advice.schedule.models.firebase.FirebaseTag
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class FiltersViewModel : ViewModel(), KoinComponent {


    private val repository by inject<FiltersRepository>()

    val state = flow {
        emit(FiltersScreenState.Init)
        repository.tags.collect {
            emit(FiltersScreenState.Success(it))
        }
    }

    fun toggle(tag: FirebaseTag) {
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

