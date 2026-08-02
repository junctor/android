package com.advice.schedule.data.repositories

import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.withFeedbackGating
import com.advice.data.sources.FeedbackDataSource
import com.advice.data.sources.MenuDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MenuRepository(
    private val menuDataSource: MenuDataSource,
    private val feedbackDataSource: FeedbackDataSource,
) {
    fun get(): Flow<FlowResult<List<Menu>>> =
        combine(
            menuDataSource.get(),
            feedbackDataSource.get(),
        ) { menus, forms ->
            when (menus) {
                is FlowResult.Failure -> menus
                FlowResult.Loading -> menus
                is FlowResult.Success ->
                    FlowResult.Success(
                        menus.value.withFeedbackGating(forms),
                    )
            }
        }
}
