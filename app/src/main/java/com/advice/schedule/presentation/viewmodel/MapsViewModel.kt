package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.MapRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MapsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<MapRepository>()

    val maps = repository.maps
}
