package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.data.repositories.FeedbackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeedbackViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FeedbackRepository>()

    private val _feedbackForm = MutableStateFlow<FeedbackState>(FeedbackState.Loading)
    val feedbackForm: Flow<FeedbackState> = _feedbackForm

    fun fetchFeedbackForm(id: Long) {
        _feedbackForm.value = FeedbackState.Loading
        viewModelScope.launch {
            val form = repository.getFeedbackForm(id)
            if (form != null) {
                _feedbackForm.value = FeedbackState.Success(form)
            } else {
                _feedbackForm.value = FeedbackState.Error
            }
        }
    }

    fun onValueChanged(item: FeedbackItem, value: String) {
        val state = _feedbackForm.value as? FeedbackState.Success ?: return

        val items = state.feedback.items.map {
            if (it.id == item.id) {
                when (val type = it.type) {
                    FeedbackType.DisplayOnly -> it
                    is FeedbackType.MultiSelect -> {
                        val selections = if (value in type.selections) {
                            type.selections.filter { it != value }
                        } else {
                            type.selections + value
                        }
                        it.copy(type = FeedbackType.MultiSelect(type.options, selections))
                    }

                    is FeedbackType.SelectOne -> {
                        it.copy(type = FeedbackType.SelectOne(type.options, value))
                    }

                    is FeedbackType.TextBox -> {
                        it.copy(type = FeedbackType.TextBox(value))
                    }
                }
            } else {
                it
            }
        }

        _feedbackForm.value = FeedbackState.Success(state.feedback.copy(items = items))
    }
}
