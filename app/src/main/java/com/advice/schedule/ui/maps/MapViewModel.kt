package com.advice.schedule.ui.maps

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.MapRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class MapViewModel : ViewModel(), KoinComponent {

    private val repository by inject<MapRepository>()

    val maps = repository.maps

}