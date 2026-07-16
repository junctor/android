package com.advice.schedule.data.repositories

import com.advice.data.session.UserSession
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource

class InformationRepository(
    userSession: UserSession,
    vendorsDataSource: VendorsDataSource,
    villagesDataSource: VillagesDataSource,
    documentsDataSource: DocumentsDataSource,
) {
    val conference = userSession.getConference()
    val vendors = vendorsDataSource.get()
    val villages = villagesDataSource.get()
    val documents = documentsDataSource.get()
}
