package com.advice.core.local

object ReminderMinutes {
    const val DISABLED = -1
    const val DEFAULT = 20

    val eventPresets: List<Int> = listOf(DISABLED) + (5..60 step 5).toList()

    val feedbackPresets: List<Int> = listOf(DISABLED, 0) + (5..60 step 5).toList()

    fun sanitizeEvent(minutes: Int): Int = if (minutes in eventPresets) minutes else DEFAULT

    fun sanitizeFeedback(minutes: Int): Int = if (minutes in feedbackPresets) minutes else DEFAULT

    fun isDisabled(minutes: Int): Boolean = minutes == DISABLED
}
