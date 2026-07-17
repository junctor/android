package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.NewsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<NewsRepository>()

    fun getNews() = repository.get()
}
