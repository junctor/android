package com.advice.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.news.data.repositories.NewsRepository

class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    fun getNews() = repository.get()
}
