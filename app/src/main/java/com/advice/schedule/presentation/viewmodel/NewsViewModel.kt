package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.data.sources.NewsDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsViewModel : ViewModel(), KoinComponent {

    // todo: this should be the repository
    private val repository by inject<NewsDataSource>()

    fun getNews() = repository.get()
}
