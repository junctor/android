package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.core.ui.InformationState
import com.advice.schedule.data.repositories.InformationRepository
import kotlinx.coroutines.flow.combine
import org.koin.core.KoinComponent
import org.koin.core.inject

class InformationViewModel : ViewModel(), KoinComponent {

    private val repository by inject<InformationRepository>()

    val state = combine(
        repository.conference,
        repository.documents,
        repository.villages,
        repository.vendors,
    ) { conference, documents, villages, vendors ->
        InformationState(
            documents = documents,
            hasWifi = conference.code.contains("DEFCON") || conference.code.contains("TEST"),
            hasVillages = villages.isNotEmpty(),
            hasVendors = vendors.isNotEmpty(),
        )
    }
}