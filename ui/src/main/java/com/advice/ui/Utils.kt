package com.advice.ui

import com.advice.core.local.Tag

fun createTag(label: String, color: String, isSelected: Boolean = false): Tag {
    return Tag(-1, label, "", color, -1, isSelected)
}