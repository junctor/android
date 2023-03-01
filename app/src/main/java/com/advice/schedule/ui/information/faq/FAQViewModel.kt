package com.advice.schedule.ui.information.faq

import androidx.lifecycle.*
import com.advice.schedule.database.repository.FAQRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FAQRepository>()

    val faqs = repository.faqs

}