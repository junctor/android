package com.advice.core.ui

import com.advice.schedule.models.firebase.FirebaseTagType

sealed class FiltersScreenState {

    object Init : FiltersScreenState()

    class Success(val filters: List<FirebaseTagType>) : FiltersScreenState()

}
