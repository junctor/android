package com.advice.schedule.data.repositories

import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource

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