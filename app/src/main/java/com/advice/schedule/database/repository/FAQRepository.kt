package com.advice.schedule.database.repository

import com.advice.data.datasource.FAQDataSource


class FAQRepository(private val faqDataSource: FAQDataSource) {

    val faqs = faqDataSource.get()

}