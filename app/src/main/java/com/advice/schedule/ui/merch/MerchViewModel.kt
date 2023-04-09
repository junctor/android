package com.advice.schedule.ui.merch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Merch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

val data = listOf(
    Merch("DC30 Homecoming Men's T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL"), image = true),
    Merch("DC30 Homecoming Women's T-Shirt", 35, listOf("XS", "S", "L", "XL")),
    Merch(
        "DC30 Square Men's T-Shirt",
        35,
        listOf("S", "4XL", "5XL", "6XL"),
        image = true,
        count = 0
    ),
    Merch("DC30 Square Women's T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL"), image = true),
    Merch("DC30 Skull T-Shirt", 40, listOf("S", "4XL", "5XL", "6XL")),
    Merch("DC30 Signal T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
    Merch("DC30 Crown T-Shirt", 50, listOf("S", "4XL", "5XL", "6XL")),
    Merch("Pride T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
    Merch("D I S O B E Y Pin", 10, listOf(), image = true),
)

class MerchViewModel : ViewModel() {

    private val _state = MutableStateFlow<List<Merch>>(emptyList())
    val state: Flow<List<Merch>> = _state

    init {
        _state.value = (data)
    }

    fun addMerch(merch: Merch) {
        viewModelScope.launch {
            val temp = _state.value.toMutableList()
            val indexOf = temp.indexOf(merch)
            if (indexOf != -1) {
                val n = temp[indexOf].copy(count = temp[indexOf].count + 1)
                temp[indexOf] = n
                Timber.e("addMerch: $temp")
                _state.emit(temp)
            }
        }
    }

    fun removeMerch(merch: Merch) {
        viewModelScope.launch {
            val temp = _state.value.toMutableList()
            val indexOf = temp.indexOf(merch)
            if (indexOf != -1) {
                val n = temp[indexOf].copy(count = temp[indexOf].count - 1)
                temp[indexOf] = n
                Timber.e("removeMerch: $temp")
                _state.emit(temp)
            }
        }
    }
}



