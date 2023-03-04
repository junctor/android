package com.advice.schedule.ui.tablet

import com.advice.core.local.Conference
import com.advice.core.local.TagType
import com.advice.core.local.Article
import com.advice.core.local.Event

data class WideScreenState(
    val conference: Conference,
    val articles: List<Article>,
    val schedule: List<Event>,
    val tags: List<TagType>,
)