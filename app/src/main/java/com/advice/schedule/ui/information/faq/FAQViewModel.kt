package com.advice.schedule.ui.information.faq

import androidx.lifecycle.*
import com.advice.core.utils.Response
import com.advice.schedule.dObj
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.repository.FAQRepository
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import com.advice.schedule.toFAQ
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FAQRepository>()

    val faqs = repository.faqs

}