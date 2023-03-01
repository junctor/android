package com.advice.schedule.database.repository

import com.advice.data.datasource.EventsDataSource
import com.advice.data.datasource.SpeakersDataSource

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
    eventsDataSource: EventsDataSource
) {

    val list = speakersDataSource.get()

    val events = eventsDataSource.get()

}