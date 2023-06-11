package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.MapRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class MapViewModel : ViewModel(), KoinComponent {

    private val repository by inject<MapRepository>()

    val maps = repository.maps

}