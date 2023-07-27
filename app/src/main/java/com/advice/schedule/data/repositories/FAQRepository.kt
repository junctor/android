package com.advice.schedule.data.repositories

import com.advice.data.sources.FAQDataSource

class FAQRepository(private val faqDataSource: FAQDataSource) {

    val faqs = faqDataSource.get()
}
