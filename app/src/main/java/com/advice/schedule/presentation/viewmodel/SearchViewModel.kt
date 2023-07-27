package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.data.repositories.SearchRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel : ViewModel(), KoinComponent {

    private val searchRepository: SearchRepository by inject()

    val state = searchRepository.state

    fun search(query: String) {
        viewModelScope.launch {
            searchRepository.search(query)
        }
    }
}
