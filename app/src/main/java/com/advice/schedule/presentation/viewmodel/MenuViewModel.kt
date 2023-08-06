package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.MenuRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MenuViewModel : ViewModel(), KoinComponent {

    private val menuRepository by inject<MenuRepository>()

    val menu = menuRepository.menu
}