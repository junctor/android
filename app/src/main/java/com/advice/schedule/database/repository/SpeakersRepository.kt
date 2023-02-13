package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.EventsDataSource
import com.advice.schedule.database.datasource.SpeakersDataSource

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
    eventsDataSource: EventsDataSource
) {

    val list = speakersDataSource.get()

    val events = eventsDataSource.get()

}