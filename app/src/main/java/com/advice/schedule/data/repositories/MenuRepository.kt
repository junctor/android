package com.advice.schedule.data.repositories

import com.advice.data.sources.MenuDataSource

class MenuRepository(
    private val menuDataSource: MenuDataSource,
) {
    fun get() = menuDataSource.get()
}
