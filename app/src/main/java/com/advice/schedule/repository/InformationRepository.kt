package com.advice.schedule.repository

import com.advice.data.UserSession
import com.advice.data.datasource.DocumentsDataSource
import com.advice.data.datasource.VendorsDataSource
import com.advice.data.datasource.VillagesDataSource

class InformationRepository(
    private val userSession: UserSession,
    private val vendorsDataSource: VendorsDataSource,
    private val villagesDataSource: VillagesDataSource,
    private val documentsDataSource: DocumentsDataSource,
) {
    val conference = userSession.getConference()
    val vendors = vendorsDataSource.get()
    val villages = villagesDataSource.get()
    val documents = documentsDataSource.get()
}