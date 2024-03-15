package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.FAQRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FAQRepository>()

    val faqs = repository.faqs
}
