package com.advice.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.search.data.repositories.SearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    val conference = searchRepository.conference
    val state = searchRepository.state

    fun search(query: String) {
        viewModelScope.launch {
            searchRepository.search(query)
        }
    }
}
