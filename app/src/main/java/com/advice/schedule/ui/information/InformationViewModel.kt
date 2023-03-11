package com.advice.schedule.ui.information

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.InformationRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class InformationViewModel : ViewModel(), KoinComponent {

    private val viewModel by inject<InformationRepository>()

    val conference = viewModel.conference

}