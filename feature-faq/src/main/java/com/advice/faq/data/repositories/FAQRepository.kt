package com.advice.faq.data.repositories

import com.advice.data.sources.FAQDataSource

class FAQRepository(
    faqDataSource: FAQDataSource,
) {
    val faqs = faqDataSource.get()
}
