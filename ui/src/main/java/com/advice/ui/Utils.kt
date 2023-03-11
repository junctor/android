package com.advice.ui

import com.advice.core.local.Tag

fun createTag(label: String, color: String, isSelected: Boolean = false): Tag {
    return Tag(-1, label, "", color, -1, isSelected)
}

fun String.indexesOf(element: String): List<Int> {
    val positions = mutableListOf<Int>()
    var index = indexOf(element)
    while (index != -1) {
        positions.add(index)
        index = indexOf(element, index + element.length)
    }
    return positions
}