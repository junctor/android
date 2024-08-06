package com.advice.core.local

data class Menu(
    val id: Long,
    val label: String,
    val items: List<MenuItem>,
) {
    companion object {
        val LOADING = Menu(-1, "Loading", emptyList())
        val ERROR = Menu(-1, "Error", emptyList())
    }
}
