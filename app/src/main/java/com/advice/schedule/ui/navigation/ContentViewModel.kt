package com.advice.schedule.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.ui.screens.ContentScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ContentViewModel :
    ViewModel(),
    KoinComponent {
    private val repository by inject<ContentRepository>()

    private val _state = MutableStateFlow<ContentScreen>(ContentScreen(emptyList()))
    val state: Flow<ContentScreen> = _state

    init {
        viewModelScope.launch {
            repository.content.collect {
                _state.value = ContentScreen(it.content)
            }
        }
    }

    fun getContent(conference: String?, id: Long?): Flow<Content?> {
        return flow {
            if (conference == null || id == null) {
                Timber.e("Conference or id is null")
                emit(null)
                return@flow
            }

            val value = repository.getContent(conference, id)
            emit(value)
        }
    }
}
