package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.SpeakersDataSource

class SpeakersRepository(private val speakersDataSource: SpeakersDataSource) {

//    val query = flow { }

    val list = speakersDataSource.get()

}