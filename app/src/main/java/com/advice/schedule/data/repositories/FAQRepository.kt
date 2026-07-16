package com.advice.schedule.data.repositories

import com.advice.data.sources.FAQDataSource

class FAQRepository(
    faqDataSource: FAQDataSource,
) {
    val faqs = faqDataSource.get()
}
