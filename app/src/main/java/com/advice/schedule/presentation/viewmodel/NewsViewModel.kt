package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.NewsRepository

class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    fun getNews() = repository.get()
}
