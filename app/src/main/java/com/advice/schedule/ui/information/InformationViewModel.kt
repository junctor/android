package com.advice.schedule.ui.information

import androidx.lifecycle.ViewModel
import com.advice.core.ui.InformationState
import com.advice.schedule.repository.InformationRepository
import kotlinx.coroutines.flow.combine
import org.koin.core.KoinComponent
import org.koin.core.inject

class InformationViewModel : ViewModel(), KoinComponent {

    private val repository by inject<InformationRepository>()

    val state = combine(
        repository.conference,
        repository.villages,
        repository.vendors
    ) { conference, villages, vendors ->
        InformationState(
            hasCodeOfConduct = conference.codeOfConduct != null,
            hasSupport = conference.support != null,
            hasWifi = conference.code.contains("DEFCON") || conference.code.contains("TEST"),
            hasVillages = villages.isNotEmpty(),
            hasVendors = vendors.isNotEmpty(),
        )
    }
}