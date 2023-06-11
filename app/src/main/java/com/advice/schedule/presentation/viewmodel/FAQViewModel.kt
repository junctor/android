package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.FAQRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FAQRepository>()

    val faqs = repository.faqs

}